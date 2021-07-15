package com.example.sfgjms.sender;

import com.example.sfgjms.model.HelloWorldMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

import static com.example.sfgjms.config.JmsConfig.MY_QUEUE;
import static com.example.sfgjms.config.JmsConfig.MY_SEND_RCV_QUEUE;

@RequiredArgsConstructor
@Component
@Slf4j
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage(){
        log.info("Sending Message.");
        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();
        jmsTemplate.convertAndSend(MY_QUEUE, message);
        log.info("Message sent.");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        log.info("Sending Message.");
        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello World")
                .build();
        Message receivedMessage = jmsTemplate.sendAndReceive(MY_SEND_RCV_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;
                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "com.example.sfgjms.model.HelloWorldMessage");
                    log.info("Sending Hello!");
                    return helloMessage;
                } catch (JsonProcessingException e) {
                    throw new JMSRuntimeException("Boom!");
                }
            }
        });
        System.out.println((receivedMessage.getBody(String.class)));
    }
}
