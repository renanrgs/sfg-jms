package com.example.sfgjms.listener;

import com.example.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import java.util.UUID;

import static com.example.sfgjms.config.JmsConfig.MY_QUEUE;
import static com.example.sfgjms.config.JmsConfig.MY_SEND_RCV_QUEUE;

@RequiredArgsConstructor
@Component
@Slf4j
public class HelloMessageListener {

    private final JmsTemplate jmsTemplate;

    //Payload annotation tells Spring to deserialize that property
    @JmsListener(destination = MY_QUEUE)
    public void listen(@Payload HelloWorldMessage helloWorldMessage, @Headers MessageHeaders headers, Message message){
//        log.info("Got message!");
//        log.info(helloWorldMessage.toString());
//        log.info("Message Processed");
    }


    //Payload annotation tells Spring to deserialize that property
    @JmsListener(destination = MY_SEND_RCV_QUEUE)
    public void listenForHello(@Payload HelloWorldMessage helloWorldMessage, @Headers MessageHeaders headers,
                               Message message) throws JMSException {
        HelloWorldMessage payLoadMessage = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();
        jmsTemplate.convertAndSend((Destination) message.getJMSReplyTo(), payLoadMessage);
        log.info("Message Processed");
    }
}
