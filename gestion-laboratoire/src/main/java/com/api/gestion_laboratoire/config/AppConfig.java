package com.api.gestion_laboratoire.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

import java.util.List;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

@Configuration
public class AppConfig {

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.util.*", "java.lang.Boolean"));
        return converter;
    }

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(System.getenv("CLOUDINARY_URL"));
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("mainExchange");
    }

    @Bean
    public Queue fromDependenciesQueue() {
        return new Queue("fromDependenciesQueue");
    }

    @Bean
    public Binding dependencyBinding(TopicExchange topicExchange,
            Queue fromDependenciesQueue) {
        return BindingBuilder.bind(fromDependenciesQueue)
                .to(topicExchange)
                .with("should.i.delete.labo");
    }

    @Bean
    public Queue fromDeletionEventsQueue() {
        return new Queue("fromDeletionEventsQueue");
    }

    @Bean
    public Binding bindingToDeleteLabo(TopicExchange topicExchange,
            Queue fromDeletionEventsQueue) {
        return BindingBuilder.bind(fromDeletionEventsQueue)
                .to(topicExchange)
                .with("delete.labo");
    }

    @Bean
    public Queue doesLaboratoireExistQueue() {
        return new Queue("doesLaboratoireExistQueue");
    }

    @Bean
    public Binding bindingToDoesLaboratoireExist(TopicExchange topicExchange,
            Queue doesLaboratoireExistQueue) {
        return BindingBuilder.bind(doesLaboratoireExistQueue)
                .to(topicExchange)
                .with("labo.exists.*");

    }
}
