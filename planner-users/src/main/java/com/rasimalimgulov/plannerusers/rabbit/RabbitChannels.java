package com.rasimalimgulov.plannerusers.rabbit;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Supplier;


//Каналы для общения
@Configuration
@Getter
public class RabbitChannels {
    //для того чтоб считывать данные по требованию (а не постоянно) - создаём поток откуда данные уже отправляются в каналы CloudStreams
    //Создаём внутреннюю шину, из которой будем отправлять сообщения в SCS (по требованию)
    private Sinks.Many<Message<Long>> innerBus = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE,false);

    @Bean
    public Supplier<Flux<Message<Long>>> newUserActionProduce(){
        return ()-> innerBus.asFlux();
    }
}
