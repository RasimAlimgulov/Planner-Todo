package com.rasimalimgulov.plannertodo.rabbitmq;

import com.rasimalimgulov.plannertodo.service.TestDataService;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Getter
public class RabbitConsumer {

    private final TestDataService testDataService;
    public RabbitConsumer(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @Bean
    public Consumer<Message<Long>> newUserActionConsume() {
        return message -> testDataService.initTestData(message.getPayload());
    }
}