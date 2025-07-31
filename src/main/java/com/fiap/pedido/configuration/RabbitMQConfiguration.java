package com.fiap.pedido.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.queue.durable:true}")
    private boolean durable;

    @Bean
    public Queue queue() {
        return new Queue(queueName, durable);
    }

}
