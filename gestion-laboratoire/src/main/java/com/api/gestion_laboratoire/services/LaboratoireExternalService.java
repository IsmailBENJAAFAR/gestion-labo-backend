package com.api.gestion_laboratoire.services;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class LaboratoireExternalService {

    RabbitTemplate rabbitTemplate;
    TopicExchange topicExchange;

    public LaboratoireExternalService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
    }

    public void sendReqToDependencies(Long id) {
        rabbitTemplate.convertAndSend(topicExchange.getName(), "labo.delete.this", id);
    }

    @RabbitListener(queues = "fromAnalyseQueue")
    public void getLaboDeletionResponseFromAnalyse(int response) {
        if (response == 0) {
            System.out.println("no dependency");
        } else if (response == -1) {
            System.out.println("THERE IS dependency");
        }
    }

}