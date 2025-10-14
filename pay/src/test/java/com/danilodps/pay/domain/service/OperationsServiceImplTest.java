package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.config.KafkaEventProducer;
import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.repository.TransactionRepository;
import com.danilodps.pay.domain.repository.UserRepository;
import com.danilodps.pay.domain.service.impl.OperationsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationsServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    StoreRepository storeRepository;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    KafkaEventProducer kafkaEventProducer;
    @InjectMocks
    OperationsService operationsService;

    @BeforeEach
    void setUp(){
        operationsService = new OperationsServiceImpl(userRepository, storeRepository, transactionRepository, kafkaEventProducer);
    }
}
