package com.skillsync.sessionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SESSION_EXCHANGE = "session.exchange";

    public static final String SESSION_BOOKED_KEY    = "session.booked";
    public static final String SESSION_ACCEPTED_KEY  = "session.accepted";
    public static final String SESSION_REJECTED_KEY  = "session.rejected";
    public static final String SESSION_CANCELLED_KEY = "session.cancelled";

    // Queues
    public static final String SESSION_BOOKED_QUEUE    = "session.booked.queue";
    public static final String SESSION_ACCEPTED_QUEUE  = "session.accepted.queue";
    public static final String SESSION_REJECTED_QUEUE  = "session.rejected.queue";
    public static final String SESSION_CANCELLED_QUEUE = "session.cancelled.queue";

    // Exchange
    @Bean
    public TopicExchange sessionExchange() {
        return new TopicExchange(SESSION_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue bookedQueue() {
        return QueueBuilder.durable(SESSION_BOOKED_QUEUE).build();
    }

    @Bean
    public Queue acceptedQueue() {
        return QueueBuilder.durable(SESSION_ACCEPTED_QUEUE).build();
    }

    @Bean
    public Queue rejectedQueue() {
        return QueueBuilder.durable(SESSION_REJECTED_QUEUE).build();
    }

    @Bean
    public Queue cancelledQueue() {
        return QueueBuilder.durable(SESSION_CANCELLED_QUEUE).build();
    }

    // Bindings
    @Bean
    public Binding bookedBinding() {
        return BindingBuilder
                .bind(bookedQueue())
                .to(sessionExchange())
                .with(SESSION_BOOKED_KEY);
    }

    @Bean
    public Binding acceptedBinding() {
        return BindingBuilder
                .bind(acceptedQueue())
                .to(sessionExchange())
                .with(SESSION_ACCEPTED_KEY);
    }

    @Bean
    public Binding rejectedBinding() {
        return BindingBuilder
                .bind(rejectedQueue())
                .to(sessionExchange())
                .with(SESSION_REJECTED_KEY);
    }

    @Bean
    public Binding cancelledBinding() {
        return BindingBuilder
                .bind(cancelledQueue())
                .to(sessionExchange())
                .with(SESSION_CANCELLED_KEY);
    }

    // JSON Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Rabbit Template
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}