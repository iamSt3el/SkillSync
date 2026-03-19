package com.skillsync.mentorservice.Messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange name — one exchange for all mentor events
    public static final String MENTOR_EXCHANGE = "mentor.exchange";

    // Queue names
    public static final String MENTOR_APPLIED_QUEUE = "mentor.applied.queue";
    public static final String MENTOR_APPROVED_QUEUE = "mentor.approved.queue";

    // Routing keys — used to route messages to correct queue
    public static final String MENTOR_APPLIED_ROUTING_KEY = "mentor.applied";
    public static final String MENTOR_APPROVED_ROUTING_KEY = "mentor.approved";

    // Declare the exchange — Topic exchange allows pattern based routing
    @Bean
    public TopicExchange mentorExchange() {
        return new TopicExchange(MENTOR_EXCHANGE);
    }

    // Declare queues
    @Bean
    public Queue mentorAppliedQueue() {
        return new Queue(MENTOR_APPLIED_QUEUE, true); // true = durable
    }

    @Bean
    public Queue mentorApprovedQueue() {
        return new Queue(MENTOR_APPROVED_QUEUE, true);
    }

    // Bind queues to exchange with routing keys
    @Bean
    public Binding mentorAppliedBinding() {
        return BindingBuilder
                .bind(mentorAppliedQueue())
                .to(mentorExchange())
                .with(MENTOR_APPLIED_ROUTING_KEY);
    }

    @Bean
    public Binding mentorApprovedBinding() {
        return BindingBuilder
                .bind(mentorApprovedQueue())
                .to(mentorExchange())
                .with(MENTOR_APPROVED_ROUTING_KEY);
    }

    // Convert messages to JSON automatically
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Configure RabbitTemplate to use JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
