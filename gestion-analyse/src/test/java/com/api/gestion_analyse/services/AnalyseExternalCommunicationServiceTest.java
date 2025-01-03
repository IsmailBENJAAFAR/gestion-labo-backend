package com.api.gestion_analyse.services;

import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.repositores.AnalyseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AnalyseExternalCommunicationServiceTest {

    private AnalyseExternalCommunicationService analyseExternalCommunicationService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private TopicExchange topicExchange;

    @Mock
    private AnalyseRepository analyseRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        analyseExternalCommunicationService = new AnalyseExternalCommunicationService(rabbitTemplate, topicExchange,
                analyseRepository);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCheckDependencyWithLaboratoireShouldBeTrue() throws JsonProcessingException {
        Long laboId = 2L;
        Map<String, Object> map = new HashMap<>();
        map.put("laboId", laboId);
        map.put("operation", "delete");
        Analyse analyse = new Analyse(1L, "MRI", new String(new byte[1000]), laboId);
        BDDMockito.when(analyseRepository.findAll()).thenReturn(List.of(analyse));
        analyseExternalCommunicationService.checkDependencyWithLabo(objectMapper.writeValueAsString(map));

        map.put("isDependent", true);
        BDDMockito.then(rabbitTemplate).should().convertAndSend(topicExchange.getName(), "should.i.delete.labo",
                objectMapper.writeValueAsString(map));
    }

    @Test
    void testCheckDependencyWithLaboratoireShouldBeFalse() throws JsonProcessingException {
        Long laboId = 2L;
        Map<String, Object> map = new HashMap<>();
        map.put("laboId", laboId);
        map.put("operation", "delete");
        Analyse analyse = new Analyse(1L, "MRI", new String(new byte[1000]), 999L);
        BDDMockito.when(analyseRepository.findAll()).thenReturn(List.of(analyse));
        analyseExternalCommunicationService.checkDependencyWithLabo(objectMapper.writeValueAsString(map));

        map.put("isDependent", false);
        BDDMockito.then(rabbitTemplate).should().convertAndSend(topicExchange.getName(), "should.i.delete.labo",
                objectMapper.writeValueAsString(map));
    }

    @Test
    void testCheckIfLaboratoireExists() throws JsonProcessingException {
        Long laboId = 2L;
        String payload = objectMapper.writeValueAsString(Map.of("laboId", laboId));
        BDDMockito.when(rabbitTemplate.convertSendAndReceive(topicExchange.getName(), "labo.exists.analyse", payload))
                .thenReturn(true);
        assertTrue(analyseExternalCommunicationService.checkIfLaboratoireExists(2L));
    }

    @Test
    void testCheckIfLaboratoireDoesNotExists() throws JsonProcessingException {
        Long laboId = 2L;
        String payload = objectMapper.writeValueAsString(Map.of("laboId", laboId));
        BDDMockito.when(rabbitTemplate.convertSendAndReceive(topicExchange.getName(), "labo.exists.analyse", payload))
                .thenReturn(false);
        assertFalse(analyseExternalCommunicationService.checkIfLaboratoireExists(2L));
    }
}