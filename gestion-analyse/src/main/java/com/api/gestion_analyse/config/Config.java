package com.api.gestion_analyse.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("mainExchange");
    }

    @Bean
    public Queue fromLaboratoireAnalyseQueue() {
        return new Queue("fromLaboratoireAnalyseQueue");
    }

    @Bean
    public Binding bindingToAnalyse(TopicExchange topicExchange,
                             Queue fromLaboratoireAnalyseQueue) {
        return BindingBuilder.bind(fromLaboratoireAnalyseQueue)
                .to(topicExchange)
                .with("labo.delete.*");
    }

}
