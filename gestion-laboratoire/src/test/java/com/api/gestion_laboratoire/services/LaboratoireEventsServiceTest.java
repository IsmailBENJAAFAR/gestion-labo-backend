package com.api.gestion_laboratoire.services;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.api.gestion_laboratoire.models.Laboratoire;
import com.api.gestion_laboratoire.repositories.LaboratoireRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class LaboratoireEventsServiceTest {

    private LaboratoireEventsService laboratoireEventsService;

    @Mock
    private RabbitTemplate mockRabbitTemplate;

    @Mock
    private TopicExchange mockTopicExchange;

    @Mock
    private LaboratoireRepository laboratoireRepository;

    ObjectMapper mapper;

    @BeforeEach
    void setup() {
        this.laboratoireEventsService = new LaboratoireEventsService(mockRabbitTemplate, mockTopicExchange,
                laboratoireRepository);
        this.mapper = new ObjectMapper();
    }

    @Test
    void testAttemptDeleteLaboratoire() throws JsonProcessingException {
        Long id = 1L;
        when(mockTopicExchange.getName()).thenReturn("topic");
        laboratoireEventsService.attemptDeleteLaboratoire(id);

        String payload = mapper.writeValueAsString(Map.of("laboId", id, "operation", "delete"));
        ArgumentCaptor<MessagePostProcessor> messagePostProcessorArgumentCaptor = ArgumentCaptor
                .forClass(MessagePostProcessor.class);
        verify(mockRabbitTemplate, times(1)).convertAndSend(eq(mockTopicExchange.getName()), eq("labo.delete.this"),
                eq(payload), messagePostProcessorArgumentCaptor.capture());

        MessagePostProcessor messagePostProcessor = messagePostProcessorArgumentCaptor.getValue();
        Message message = new Message(new byte[0]);
        message = messagePostProcessor.postProcessMessage(message);
        assertEquals("0", message.getMessageProperties().getExpiration());
    }

    @Test
    void testCanIDeleteUtilFunctShouldSendAFalseMessageIfThereIsAtLeastOneDependency() throws JsonProcessingException {

        ReflectionTestUtils.setField(laboratoireEventsService, "numberOfDependencies", 2);
        Long id = 1L;
        laboratoireEventsService.attemptDeleteLaboratoire(id);

        when(mockTopicExchange.getName()).thenReturn("topic");
        Map<String, Object> map = new HashMap<>();
        map.put("laboId", id);
        map.put("operation", "delete");
        map.put("isDependent", true);

        // first message from one of the dependencies
        laboratoireEventsService.canIDeleteUtilFunct(mapper.writeValueAsString(map));

        // second message from one of the dependencies
        map.computeIfPresent("isDependent", (k, v) -> v = false);
        laboratoireEventsService.canIDeleteUtilFunct(mapper.writeValueAsString(map));

        ArgumentCaptor<MessagePostProcessor> messagePostProcessorArgumentCaptor = ArgumentCaptor
                .forClass(MessagePostProcessor.class);
        verify(mockRabbitTemplate, times(1)).convertAndSend(eq(mockTopicExchange.getName()), eq("delete.labo"),
                eq(false), messagePostProcessorArgumentCaptor.capture());

        MessagePostProcessor messagePostProcessor = messagePostProcessorArgumentCaptor.getValue();
        Message message = new Message(new byte[0]);
        message = messagePostProcessor.postProcessMessage(message);
        assertEquals("3000", message.getMessageProperties().getExpiration());
    }

    @Test
    void testCanIDeleteUtilFunctShouldSendATrueMessageIfThereAreNoDependencies()
            throws JsonProcessingException {

        ReflectionTestUtils.setField(laboratoireEventsService, "numberOfDependencies", 2);
        Long id = 1L;
        laboratoireEventsService.attemptDeleteLaboratoire(id);

        Map<String, Object> map = new HashMap<>();
        map.put("laboId", id);
        map.put("operation", "delete");

        when(mockTopicExchange.getName()).thenReturn("topic");
        // first message from one of the dependencies
        map.put("isDependent", false);
        laboratoireEventsService.canIDeleteUtilFunct(mapper.writeValueAsString(map));

        // second message from one of the dependencies
        laboratoireEventsService.canIDeleteUtilFunct(mapper.writeValueAsString(map));

        ArgumentCaptor<MessagePostProcessor> messagePostProcessorArgumentCaptor = ArgumentCaptor
                .forClass(MessagePostProcessor.class);

        verify(mockRabbitTemplate, times(1)).convertAndSend(eq(mockTopicExchange.getName()), eq("delete.labo"),
                eq(true), messagePostProcessorArgumentCaptor.capture());

        MessagePostProcessor messagePostProcessor = messagePostProcessorArgumentCaptor.getValue();
        Message message = new Message(new byte[0]);
        message = messagePostProcessor.postProcessMessage(message);
        assertEquals("3000", message.getMessageProperties().getExpiration());
    }

    @Test
    void testCanDeleteLaboratoireWhenAllGood() throws JsonProcessingException {
        when(mockRabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000)).thenReturn(true);
        assertTrue(laboratoireEventsService.canDeleteLaboratoire(1L));

        when(mockRabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000)).thenReturn(false);
        assertFalse(laboratoireEventsService.canDeleteLaboratoire(1L));
    }

    @Test
    void testCanDeleteLaboratoireWhenError() throws JsonProcessingException {
        when(mockRabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000)).thenReturn(null);
        assertNull(laboratoireEventsService.canDeleteLaboratoire(1L));
    }

    @Test
    void testCanDeleteLaboratoireWhenAmqpException() throws JsonProcessingException {
        Long id = 1L;
        LaboratoireEventsService laboratoireEventsServiceSpy = spy(this.laboratoireEventsService);
        doThrow(new AmqpException("Some error")).when(laboratoireEventsServiceSpy).attemptDeleteLaboratoire(id);
        assertNull(laboratoireEventsServiceSpy.canDeleteLaboratoire(id));
    }

    @Test
    void testWhenLaboratoireExists() throws JsonProcessingException {
        Long laboId = 1L;
        Laboratoire laboDummy = new Laboratoire("help", "me", false, LocalDate.of(2024, 1, 1));
        when(laboratoireRepository.findById(laboId)).thenReturn(Optional.of(laboDummy));

        assertTrue(laboratoireEventsService.doesLaboExist(mapper.writeValueAsString(Map.of("laboId", laboId))));
    }

    @Test
    void testWhenLaboratoireDoesNotExist() throws JsonProcessingException {
        Long laboId = 1L;
        when(laboratoireRepository.findById(laboId)).thenReturn(Optional.empty());
        assertFalse(laboratoireEventsService.doesLaboExist(mapper.writeValueAsString(Map.of("laboId", laboId))));
    }
}