package com.api.gestion_analyse.services;

import java.util.HashMap;
import java.util.Map;

import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.repositores.AnalyseRepository;
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

    public AnalyseExternalCommunicationService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange, AnalyseRepository analyseRepository) {
        this.analyseRepository = analyseRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
    }

    public JSONObject getLaboWithId(Long id) {
        try {
            HttpResponse<JsonNode> resp = Unirest.get("http://gestionlabo:8080/api/v1/laboratoires/" + id).asJson();
            return resp.isSuccess() ? resp.getBody().getObject() : new JSONObject();
        } catch (UnirestException e) {
            return null;
        }
    }

    public Map<Long, JSONObject> getAllLabos() {
        Map<Long, JSONObject> map = new HashMap<>();
        try {
            HttpResponse<JsonNode> resp = Unirest.get("http://gestionlabo:8080/api/v1/laboratoires").asJson();
            for (Object labo : resp.getBody().getArray()) {
                JSONObject parsedLabo = (JSONObject) labo;
                map.put(parsedLabo.getLong("id"), parsedLabo);
            }
            return map;
        } catch (UnirestException e) {
            return null;
        }
    }

    @RabbitListener(queues = "#{fromLaboratoireAnalyseQueue.name}")
    public void checkDependencyWithLabo(Long id){
        for (Analyse analyse : analyseRepository.findAll()) {
            if (analyse.getFkIdLaboratoire().equals(id)) {
                rabbitTemplate.convertAndSend(topicExchange.getName(), "should.i.analyse.delete.labo",1);
                return;
            }
        }
        rabbitTemplate.convertAndSend(topicExchange.getName(), "should.i.analyse.delete.labo",0);
    }
}
