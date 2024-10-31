package com.rasimalimgulov.plannerusers.rabbit;

import org.springframework.messaging.MessageChannel;

public interface TodoBinding {
String OUTPUT_CHANNEL = "todoOutputChannel";
MessageChannel todoOutputChannel();
}
