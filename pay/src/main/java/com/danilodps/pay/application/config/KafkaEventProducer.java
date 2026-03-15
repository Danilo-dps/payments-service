package com.danilodps.pay.application.config;

import com.danilodps.commons.domain.model.response.DepositResponse;
import com.danilodps.commons.domain.model.response.SignInResponse;
import com.danilodps.commons.domain.model.response.SignUpResponse;
import com.danilodps.commons.domain.model.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    @Value("${spring.kafka.producer.topic.kafka-deposit}")
    public String depositCreated;

    @Value("${spring.kafka.producer.topic.kafka-transfer}")
    public String transferCreated;

    @Value("${spring.kafka.producer.topic.kafka-signup}")
    public String signUpNotification;

    @Value("${spring.kafka.producer.topic.kafka-signin}")
    public String signInNotification;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDepositEventNotification(DepositResponse depositResponse){
        log.info("Enviando comprovante de depósito");
        kafkaTemplate.send(depositCreated, depositResponse.depositId(), depositResponse);
    }

    public void publishTransferEventNotification(TransactionResponse transactionResponse){
        log.info("Enviando comprovante de transferência");
        kafkaTemplate.send(transferCreated, transactionResponse.transactionId(), transactionResponse);
    }

    public void publishSignInNotification(SignInResponse signinResponse){
        log.info("Criação de usuário");
        kafkaTemplate.send(signInNotification, signinResponse.id(), signinResponse);
    }

    public void publishSignUpNotification(SignUpResponse signupResponse ){
        log.info("Usuário logado");
        kafkaTemplate.send(signUpNotification, signupResponse.id(), signupResponse);
    }

}
