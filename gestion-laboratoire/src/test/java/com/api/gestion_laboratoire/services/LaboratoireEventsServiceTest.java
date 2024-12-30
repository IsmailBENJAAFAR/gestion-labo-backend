package com.api.gestion_laboratoire.services;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class LaboratoireEventsServiceTest {

    private LaboratoireEventsService laboratoireEventsService;

    @Mock
    private RabbitTemplate mockRabbitTemplate;

    @Mock
    private TopicExchange mockTopicExchange;

    ObjectMapper mapper;

    @BeforeEach
    void setup() {
        this.laboratoireEventsService = new LaboratoireEventsService(mockRabbitTemplate, mockTopicExchange);
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
        assertTrue(message.getMessageProperties().getExpiration().equals("0"));
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
            throws JsonMappingException, JsonProcessingException {

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
}