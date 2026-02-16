package com.danilodps.pay.domain.service;

//import com.danilodps.pay.domain.config.KafkaEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationsServiceImplTest {

//    @Mock
//    KafkaEventProducer kafkaEventProducer;
    @InjectMocks
    OperationsService operationsService;

    @BeforeEach
    void setUp(){}

}
