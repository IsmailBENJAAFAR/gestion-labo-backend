package com.api.gestion_laboratoire.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LaboratoireEventsService {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    private final ObjectMapper objectMapper;
    @Value("${dependency.count}")
    private int numberOfDependencies;
    private Hashtable<Long, List<Boolean>> entriesToDelete;

    public LaboratoireEventsService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
        this.entriesToDelete = new Hashtable<>();
        this.objectMapper = new ObjectMapper();
    }

    public void attemptDeleteLaboratoire(Long id) throws JsonProcessingException {
        this.entriesToDelete.put(id, new ArrayList<>());
        String stringJsonPayload = objectMapper.writeValueAsString(Map.of("laboId", id, "operation", "delete"));
        rabbitTemplate.convertAndSend(topicExchange.getName(), "labo.delete.this", stringJsonPayload, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(0));
            return message;
        });
    }

    @RabbitListener(queues = "fromDependenciesQueue")
    public void canIDeleteUtilFunct(String dependencyResponseJson)
            throws JsonMappingException, JsonProcessingException {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        Map<String, Object> dependencyResponse = objectMapper.readValue(dependencyResponseJson, typeRef);
        String operation = (String) dependencyResponse.get("operation");

        System.out.println(dependencyResponse);

        if (operation.equals("delete")) {
            Long laboId = Long.valueOf((Integer) dependencyResponse.get("laboId"));
            Boolean isDependent = (Boolean) dependencyResponse.get("isDependent");
            System.out.println(laboId + "=>" + isDependent);
            try {
                this.entriesToDelete.get(laboId).add(isDependent);
            } catch (NullPointerException e) {
                return;
            }
            System.out.println(this.entriesToDelete);

            for (Entry<Long, List<Boolean>> entry : this.entriesToDelete.entrySet()) {
                Boolean canDelete = true;
                if (entry.getValue().size() == numberOfDependencies) {
                    for (Boolean flag : entry.getValue()) {
                        if (flag) {
                            canDelete = false;
                            this.entriesToDelete.remove(entry.getKey());
                            rabbitTemplate.convertAndSend(topicExchange.getName(), "delete.labo",
                                    canDelete, message -> {
                                        message.getMessageProperties().setExpiration(String.valueOf(3000));
                                        return message;
                                    });
                            break;
                        }
                    }
                    if (canDelete) {
                        this.entriesToDelete.remove(entry.getKey());
                        rabbitTemplate.convertAndSend(topicExchange.getName(), "delete.labo",
                                canDelete, message -> {
                                    message.getMessageProperties().setExpiration(String.valueOf(3000));
                                    return message;
                                });
                    }
                }
            }
        }
    }

    public Boolean canDeleteLaboratoire(Long id) throws JsonProcessingException {
        try {
            attemptDeleteLaboratoire(id);
        } catch (AmqpException e) {
            return null;
        }
        return (Boolean) rabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000);
    }

}
