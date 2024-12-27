package com.api.gestion_laboratoire.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class LaboratoireEventsService {
    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    private Map<Long, List<Integer>> entriesToDelete;

    public LaboratoireEventsService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
        this.entriesToDelete = new HashMap<>();
    }

    public void deleteLaboratoire(Long id) {
        entriesToDelete.put(id, new ArrayList<Integer>());
    }

    @RabbitListener(queues = "fromDependenciesQueue")
    public void deleteUtilFunct(Map<Long, Integer> dependencyResponse) {
        System.out.println(dependencyResponse.values());
    }

}
