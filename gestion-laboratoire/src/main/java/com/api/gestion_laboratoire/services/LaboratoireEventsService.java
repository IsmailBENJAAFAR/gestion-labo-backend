package com.api.gestion_laboratoire.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.api.gestion_laboratoire.repositories.LaboratoireRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Nullable;

@Service
public class LaboratoireEventsService {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    private final ObjectMapper objectMapper;
    private final LaboratoireRepository laboratoireRepository;
    @Value("${dependency.count}")
    private int numberOfDependencies;
    private Hashtable<Long, List<Boolean>> entriesToDelete;
    private static final String MAIN_FIELD_NAME = "laboId";

    public LaboratoireEventsService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange,
            LaboratoireRepository laboratoireRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
        this.laboratoireRepository = laboratoireRepository;
        this.entriesToDelete = new Hashtable<>();
        this.objectMapper = new ObjectMapper();
    }

    public void attemptDeleteLaboratoire(Long id) throws JsonProcessingException {
        this.entriesToDelete.put(id, new ArrayList<>());
        String stringJsonPayload = objectMapper.writeValueAsString(Map.of(MAIN_FIELD_NAME, id, "operation", "delete"));
        rabbitTemplate.convertAndSend(topicExchange.getName(), "labo.delete.this", stringJsonPayload, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(0));
            return message;
        });
    }

    @RabbitListener(queues = "fromDependenciesQueue")
    public void canIDeleteUtilFunct(String dependencyResponseJson)
            throws JsonProcessingException {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        Map<String, Object> dependencyResponse = objectMapper.readValue(dependencyResponseJson, typeRef);
        String operation = (String) dependencyResponse.get("operation");

        if (operation.equals("delete")) {
            Long laboId = Long.valueOf((Integer) dependencyResponse.get(MAIN_FIELD_NAME));
            Boolean isDependent = (Boolean) dependencyResponse.get("isDependent");
            this.entriesToDelete.get(laboId).add(isDependent);

            for (Entry<Long, List<Boolean>> entry : this.entriesToDelete.entrySet()) {
                Boolean canDelete = true;
                if (entry.getValue().size() == numberOfDependencies) {
                    for (Boolean flag : entry.getValue()) {
                        if (Boolean.TRUE.equals(flag)) {
                            canDelete = false;
                            this.entriesToDelete.remove(entry.getKey());
                            sendMessage(canDelete);
                            break;
                        }
                    }
                    if (Boolean.TRUE.equals(canDelete)) {
                        sendMessage(canDelete);
                    }
                }
            }
        }
    }

    private void sendMessage(Boolean canDelete) {
        rabbitTemplate.convertAndSend(topicExchange.getName(), "delete.labo",
                canDelete, message -> {
                    message.getMessageProperties().setExpiration(String.valueOf(3000));
                    return message;
                });
    }

    @Nullable
    public Boolean canDeleteLaboratoire(Long id) throws JsonProcessingException {
        try {
            attemptDeleteLaboratoire(id);
        } catch (AmqpException e) {
            return null;
        }
        return (Boolean) rabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000);
    }

    @RabbitListener(queues = "doesLaboratoireExistQueue")
    public Boolean doesLaboExist(String dependencyRequestJson) throws JsonProcessingException {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        Map<String, Object> dependencyRequest = objectMapper.readValue(dependencyRequestJson, typeRef);
        Long id = Long.valueOf((Integer) dependencyRequest.get(MAIN_FIELD_NAME));
        return laboratoireRepository.findById(id).isPresent();
    }
}
