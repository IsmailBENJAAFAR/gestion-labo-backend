package com.api.gestion_laboratoire.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.api.gestion_laboratoire.repositories.LaboratoireRepository;

@Service
public class LaboratoireEventsService {

    private Map<Long, List<Boolean>> entriesToDelete;
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    @Value("${dependency.count}")
    private int numberOfDependencies;

    public LaboratoireEventsService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
        this.entriesToDelete = new HashMap<>();
    }

    private void attemptDeleteLaboratoire(Long id) {
        if (entriesToDelete.size() > 0) {
            entriesToDelete.clear();
        }
        entriesToDelete.put(id, new ArrayList<Boolean>());
        rabbitTemplate.convertAndSend(topicExchange.getName(), "labo.delete.this", id, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(0));
            return message;
        });
    }

    @RabbitListener(queues = "fromDependenciesQueue")
    private void canIDeleteUtilFunct(Map<Long, Boolean> dependencyResponse) {
        Long laboId = dependencyResponse.keySet().iterator().next();
        Boolean isDependent = dependencyResponse.values().iterator().next();
        System.out.println(laboId + "=>" + isDependent);
        System.out.println(entriesToDelete);
        entriesToDelete.get(laboId).add(isDependent);
        System.out.println(entriesToDelete);

        for (Entry<Long, List<Boolean>> entry : entriesToDelete.entrySet()) {
            if (entry.getValue().size() == numberOfDependencies) {
                for (Boolean flag : entry.getValue()) {
                    if (flag) {
                        entriesToDelete.remove(entry.getKey());
                        rabbitTemplate.convertAndSend(topicExchange.getName(), "delete.labo",
                                false, message -> {
                                    message.getMessageProperties().setExpiration(String.valueOf(3000));
                                    return message;
                                });
                        break;
                    }
                    entriesToDelete.remove(entry.getKey());
                    rabbitTemplate.convertAndSend(topicExchange.getName(), "delete.labo",
                            true, message -> {
                                message.getMessageProperties().setExpiration(String.valueOf(3000));
                                return message;
                            });
                }
            }
        }
    }

    public Boolean canDeleteLaboratoire(Long id) {
        attemptDeleteLaboratoire(id);
        return (Boolean) rabbitTemplate.receiveAndConvert("fromDeletionEventsQueue", 3000);
    }

}
