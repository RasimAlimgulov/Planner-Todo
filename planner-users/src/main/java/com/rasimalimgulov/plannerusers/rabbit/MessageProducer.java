package com.rasimalimgulov.plannerusers.rabbit;

import lombok.Getter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;


//Работа с каналами
@Service
@Getter
public class MessageProducer {

    private RabbitChannels rabbitChannels;

    public void sendMessage(Long id){
        rabbitChannels.getInnerBus().emitNext(MessageBuilder.withPayload(id).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        System.out.println("Message sent: "+id);
    }
}
