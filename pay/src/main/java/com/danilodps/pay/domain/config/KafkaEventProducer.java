//package com.danilodps.pay.domain.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class KafkaEventProducer {
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void publishKafkaDepositEventNotification(DepositResponse depositResponse){
//        log.info("Enviando comprovante de depósito");
//        kafkaTemplate.send(KafkaConfig.DEPOSIT_NOTIFICATION_TOPIC, depositResponse.depositId().toString(), depositResponse);
//    }
//
//    public void publishKafkaTransferEventNotification(TransactionResponse transactionResponse){
//        log.info("Enviando comprovante de transferência");
//        kafkaTemplate.send(KafkaConfig.TRANSFER_NOTIFICATION_TOPIC, transactionResponse.getTransactionId().toString(), transactionResponse);
//    }
//
//    public void publishKafkaSignInNotification(SigninResponse signinResponse){
//        log.info("Criação de usuário");
//        kafkaTemplate.send(KafkaConfig.SIGN_IN_NOTIFICATION_TOPIC, signinResponse.id().toString(), signinResponse);
//    }
//
//    public void publishKafkaSignUpNotification(SignupResponse signupResponse ){
//        log.info("Usuário logado");
//        kafkaTemplate.send(KafkaConfig.SIGN_UP_NOTIFICATION_TOPIC, signupResponse.id().toString(), signupResponse);
//    }
//}
