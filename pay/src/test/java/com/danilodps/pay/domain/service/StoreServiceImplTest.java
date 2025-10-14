package com.danilodps.pay.domain.service;

import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.utils.validations.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    StoreRepository storeRepository;

    @Mock
    EmailValidator emailValidator;

    @InjectMocks
    StoreService storeService;

    @BeforeEach
    void setUp(){

    }
}
