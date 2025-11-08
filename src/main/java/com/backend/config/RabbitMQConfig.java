package com.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String MENU_PROCESSING_QUEUE = "menu.processing.queue";
    public static final String MENU_PROCESSING_EXCHANGE = "menu.processing.exchange";
    public static final String MENU_PROCESSING_ROUTING_KEY = "menu.processing";

    @Bean
    public Queue menuProcessingQueue() {
        return QueueBuilder.durable(MENU_PROCESSING_QUEUE)
                .withArgument("x-dead-letter-exchange", "menu.dlx") // Dead letter queue
                .build();
    }

    @Bean
    public TopicExchange menuProcessingExchange() {
        return new TopicExchange(MENU_PROCESSING_EXCHANGE);
    }

    @Bean
    public Binding menuProcessingBinding(Queue menuProcessingQueue,
                                         TopicExchange menuProcessingExchange) {
        return BindingBuilder.bind(menuProcessingQueue)
                .to(menuProcessingExchange)
                .with(MENU_PROCESSING_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}