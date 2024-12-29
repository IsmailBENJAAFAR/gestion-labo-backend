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

@ExtendWith(MockitoExtension.class)
public class LaboratoireEventsServiceTest {

    private LaboratoireEventsService laboratoireEventsService;

    @Mock
    private RabbitTemplate mockRabbitTemplate;

    @Mock
    private TopicExchange mockTopicExchange;

    @BeforeEach
    void setup() {
        this.laboratoireEventsService = new LaboratoireEventsService(mockRabbitTemplate, mockTopicExchange);
    }

    @Test
    void testAttemptDeleteLaboratoire() {
        Long id = 1L;
        when(mockTopicExchange.getName()).thenReturn("topic");
        laboratoireEventsService.attemptDeleteLaboratoire(id);

        ArgumentCaptor<MessagePostProcessor> messagePostProcessorArgumentCaptor = ArgumentCaptor
                .forClass(MessagePostProcessor.class);
        verify(mockRabbitTemplate, times(1)).convertAndSend(eq(mockTopicExchange.getName()), eq("labo.delete.this"),
                eq(id), messagePostProcessorArgumentCaptor.capture());

        MessagePostProcessor messagePostProcessor = messagePostProcessorArgumentCaptor.getValue();
        Message message = new Message(new byte[0]);
        message = messagePostProcessor.postProcessMessage(message);
        assertTrue(message.getMessageProperties().getExpiration().equals("0"));
    }

    @Test
    void testCanIDeleteUtilFunctShouldSendAFalseMessageIfThereIsAtLeastOneDependency() {

        ReflectionTestUtils.setField(laboratoireEventsService, "numberOfDependencies", 2);
        Long id = 1L;
        laboratoireEventsService.attemptDeleteLaboratoire(id);

        when(mockTopicExchange.getName()).thenReturn("topic");
        // first message from one of the dependencies
        laboratoireEventsService.canIDeleteUtilFunct(Map.of(1L, true));
        // second message from one of the dependencies
        laboratoireEventsService.canIDeleteUtilFunct(Map.of(1L, false));

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
    void testCanIDeleteUtilFunctShouldSendATrueMessageIfThereAreNoDependencies() {

        ReflectionTestUtils.setField(laboratoireEventsService, "numberOfDependencies", 2);
        Long id = 1L;
        laboratoireEventsService.attemptDeleteLaboratoire(id);

        when(mockTopicExchange.getName()).thenReturn("topic");
        // first message from one of the dependencies (false => not dependent)
        laboratoireEventsService.canIDeleteUtilFunct(Map.of(1L, false));
        // second message from one of the dependencies (false => not dependent)
        laboratoireEventsService.canIDeleteUtilFunct(Map.of(1L, false));

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
    void testCanDeleteLaboratoireWhenAllGood() {
        when(mockRabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000)).thenReturn(true);
        assertTrue(laboratoireEventsService.canDeleteLaboratoire(1L));

        when(mockRabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000)).thenReturn(false);
        assertFalse(laboratoireEventsService.canDeleteLaboratoire(1L));
    }

    @Test
    void testCanDeleteLaboratoireWhenError() {
        when(mockRabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000)).thenReturn(null);
        assertNull(laboratoireEventsService.canDeleteLaboratoire(1L));
    }

    @Test
    void testCanDeleteLaboratoireWhenAmqpException() {
        Long id = 1L;
        LaboratoireEventsService laboratoireEventsServiceSpy = spy(this.laboratoireEventsService);
        doThrow(new AmqpException("Some error")).when(laboratoireEventsServiceSpy).attemptDeleteLaboratoire(id);
        assertNull(laboratoireEventsServiceSpy.canDeleteLaboratoire(id));
    }
}