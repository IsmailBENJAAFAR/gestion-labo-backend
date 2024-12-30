package com.api.gestion_analyse.services;

import java.util.HashMap;
import java.util.Map;

import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.repositores.AnalyseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;

@Service
public class AnalyseExternalCommunicationService {

    private final RabbitTemplate rabbitTemplate;
    private final AnalyseRepository analyseRepository;
    private final TopicExchange topicExchange;
    private final ObjectMapper objectMapper;

    public AnalyseExternalCommunicationService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange, AnalyseRepository analyseRepository) {
        this.analyseRepository = analyseRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setReceiveTimeout(4000);
        this.rabbitTemplate.setReplyTimeout(3000);
        this.topicExchange = topicExchange;
        this.objectMapper = new ObjectMapper();
    }


    @RabbitListener(queues = "#{fromLaboratoireAnalyseQueue.name}")
    public void checkDependencyWithLabo(String jsonPayloadFromLaboratoire) throws JsonProcessingException {
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
        Map<String,Object> payloadFromLaboratoire = this.objectMapper.readValue(jsonPayloadFromLaboratoire, typeRef);
        Map<String,Object> map = new HashMap<>();
        Long id = Long.valueOf((Integer)payloadFromLaboratoire.get("laboId"));
        map.put("laboId",id);
        map.put("operation",(String)payloadFromLaboratoire.get("operation"));
        System.out.println(map);
        for (Analyse analyse : analyseRepository.findAll()) {
            if (analyse.getFkIdLaboratoire().equals(id)) {
                map.put("isDependent",true);
                rabbitTemplate.convertAndSend(topicExchange.getName(), "should.i.delete.labo",objectMapper.writeValueAsString(map));
                return;
            }
        }
        map.put("isDependent", false);
        rabbitTemplate.convertAndSend(topicExchange.getName(), "should.i.delete.labo",objectMapper.writeValueAsString(map));
    }

    public Boolean checkIfLaboratoireExists(Long idLaboratoire) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(Map.of("laboId",idLaboratoire));
        return (Boolean) rabbitTemplate.convertSendAndReceive(topicExchange.getName(), "labo.exists.analyse",payload);
    }
}
