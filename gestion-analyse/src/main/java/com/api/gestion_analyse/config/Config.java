package com.api.gestion_analyse.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Config {

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.lang.Boolean"));
        return converter;
    }

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
