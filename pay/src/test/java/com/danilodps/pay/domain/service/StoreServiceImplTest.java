package com.danilodps.pay.domain.service;

import com.danilodps.pay.application.exceptions.NotFoundException;
import com.danilodps.pay.domain.adapter.Store2StoreDTO;
import com.danilodps.pay.domain.model.Store;
import com.danilodps.pay.domain.model.response.StoreResponse;
import com.danilodps.pay.domain.repository.StoreRepository;
import com.danilodps.pay.domain.service.impl.StoreServiceImpl;
import com.danilodps.pay.domain.utils.validations.EmailValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    StoreRepository storeRepository;

    @Mock
    EmailValidator emailValidator;

    @InjectMocks
    StoreServiceImpl storeService;

    private static final UUID storeId = UUID.fromString("6255720d-096f-404e-aa46-29d1998a378f");
    private static final String email = "teste1999@gmail.com";

    private static Store storeEntityMock(){
        Store store = new Store();
        store.setStoreId(storeId);
        store.setStoreEmail("teste1999@gmail.com");
        store.setStoreName("Bazinga");
        store.setBalance(BigDecimal.valueOf(1000));
        return store;
    }

    private static StoreResponse storeResponseMock(){
        return StoreResponse.builder()
                .storeId(storeId)
                .storeName("Novo Bazinga")
                .storeEmail("Novoemail1234@gmail.com")
                .balance(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void shouldGetStoreResponseByStoreId() {

        Store mockEntity = storeEntityMock();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockEntity));

        StoreResponse actualResponse = storeService.getById(storeId);

        assertNotNull(actualResponse);
        assertEquals("Bazinga", actualResponse.storeName());
        assertEquals(storeId, actualResponse.storeId());

        verify(storeRepository, times(1)).findById(storeId);
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    void shouldThrowExceptionWhenStoreByStoreIdNotFound() {

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            storeService.getById(storeId);
        }, "Store not found");

        verify(storeRepository, times(1)).findById(storeId);
    }

    @Test
    void shouldGetStoreResponseByEmail() {

        Store mockEntity = storeEntityMock();

        when(storeRepository.findByStoreEmail(email)).thenReturn(Optional.of(mockEntity));

        StoreResponse actualResponse = storeService.getByEmail(email);

        assertNotNull(actualResponse);
        assertEquals("Bazinga", actualResponse.storeName());
        assertEquals(email, actualResponse.storeEmail());

        verify(storeRepository, times(1)).findByStoreEmail(email);
        verifyNoMoreInteractions(storeRepository);
    }

    @Test
    void shouldThrowExceptionWhenStoreByStoreEmailNotFound() {

        when(storeRepository.findByStoreEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            storeService.getByEmail(email);
        }, "Store not found");

        verify(storeRepository, times(1)).findByStoreEmail(email);
    }

    // TODO, é preciso atualizar para permitir atualizar com base no StoreRequest
    @Test
    void shouldUpdateStore(){
        Store mockEntity = storeEntityMock();
        StoreResponse storeResponse = storeResponseMock();

        mockEntity.setStoreEmail(storeResponse.storeEmail());
        emailValidator.validate(storeResponse.storeEmail());

        lenient().when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockEntity));
        lenient().when(storeRepository.saveAndFlush(mockEntity)).thenReturn(mockEntity);
        lenient().when(storeService.update(storeId, storeResponse)).thenReturn(Store2StoreDTO.convert(mockEntity));

        Assertions.assertEquals("Novoemail1234@gmail.com", mockEntity.getStoreEmail());
    }
}
