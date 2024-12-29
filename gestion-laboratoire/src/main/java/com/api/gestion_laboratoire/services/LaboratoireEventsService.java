package com.api.gestion_laboratoire.services;

import java.util.ArrayList;
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

@Service
public class LaboratoireEventsService {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    @Value("${dependency.count}")
    private int numberOfDependencies;
    @Getter
    private Hashtable<Long, List<Boolean>> entriesToDelete;

    public LaboratoireEventsService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
        this.entriesToDelete = new Hashtable<>();
    }

    public void attemptDeleteLaboratoire(Long id) {
        this.getEntriesToDelete().put(id, new ArrayList<>());
        rabbitTemplate.convertAndSend(topicExchange.getName(), "labo.delete.this", id, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(0));
            return message;
        });
    }

    @RabbitListener(queues = "fromDependenciesQueue")
    public void canIDeleteUtilFunct(Map<Long, Boolean> dependencyResponse) {
        Long laboId = dependencyResponse.keySet().iterator().next();
        Boolean isDependent = dependencyResponse.values().iterator().next();
        System.out.println(laboId + "=>" + isDependent);
        // try {
        this.getEntriesToDelete().get(laboId).add(isDependent);
        // } catch (NullPointerException e) {
        // return;
        // }
        System.out.println(this.getEntriesToDelete());

        for (Entry<Long, List<Boolean>> entry : this.getEntriesToDelete().entrySet()) {
            if (entry.getValue().size() == numberOfDependencies) {
                for (Boolean flag : entry.getValue()) {
                    if (flag) {
                        this.getEntriesToDelete().remove(entry.getKey());
                        rabbitTemplate.convertAndSend(topicExchange.getName(), "delete.labo",
                                false, message -> {
                                    message.getMessageProperties().setExpiration(String.valueOf(3000));
                                    return message;
                                });
                        break;
                    }
                }
                this.getEntriesToDelete().remove(entry.getKey());
                rabbitTemplate.convertAndSend(topicExchange.getName(), "delete.labo",
                        true, message -> {
                            message.getMessageProperties().setExpiration(String.valueOf(3000));
                            return message;
                        });
            }
        }
    }

    public Boolean canDeleteLaboratoire(Long id) {
        try {
            attemptDeleteLaboratoire(id);
        } catch (AmqpException e) {
            return null;
        }
        return (Boolean) rabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 5000);
    }

}
