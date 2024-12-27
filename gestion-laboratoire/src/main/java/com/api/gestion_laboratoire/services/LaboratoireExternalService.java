package com.api.gestion_laboratoire.services;

import javax.naming.CommunicationException;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.api.gestion_laboratoire.repositories.LaboratoireRepository;

@Service
public class LaboratoireExternalService {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    private final LaboratoireRepository laboratoireRepository;

    public LaboratoireExternalService(RabbitTemplate rabbitTemplate, TopicExchange topicExchange,
            LaboratoireRepository laboratoireRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
        this.laboratoireRepository = laboratoireRepository;
    }

    // private Boolean isDependant(String serviceQueueName) throws CommunicationException {
    //     Integer isDependant = (Integer) rabbitTemplate.receiveAndConvert(serviceQueueName, 3000);
    //     if (isDependant == null)
    //         return null;
    //     else if (isDependant == 1)
    //         return true;
    //     else
    //         return false;
    // }

    // @RabbitListener(queues = "fromAnalyseCreateQueue")
    // public Integer getLaboId(Long id) {
    //     return laboratoireRepository.findById(id).isPresent() ? 1 : 0;
    // }

}