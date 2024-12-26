package com.api.gestion_laboratoire.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class AppConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(System.getenv("CLOUDINARY_URL"));
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("mainExchange");
    }

    @Bean
    public Queue fromUserQueue() {
        return new Queue("fromUserQueue");
    }

    @Bean
    public Queue fromAnalyseQueue() {
        return new Queue("fromAnalyseQueue");
    }

    @Bean
    public Queue fromContactQueue() {
        return new Queue("fromContactQueue");
    }

    @Bean
    public Binding utilisateurBindingToDeleteLabo(TopicExchange topicExchange,
                             Queue fromUserQueue) {
        return BindingBuilder.bind(fromUserQueue)
                .to(topicExchange)
                .with("should.i.delete.*");
    }

    @Bean
    public Binding contactBindingToDeleteLabo(TopicExchange topicExchange,
                             Queue fromContactQueue) {
        return BindingBuilder.bind(fromContactQueue)
                .to(topicExchange)
                .with("should.i.delete.*");
    }

    @Bean
    public Binding analyseBindingToDeleteLabo(TopicExchange topicExchange,
                             Queue fromAnalyseQueue) {
        return BindingBuilder.bind(fromAnalyseQueue)
                .to(topicExchange)
                .with("should.i.delete.*");
    }
}
