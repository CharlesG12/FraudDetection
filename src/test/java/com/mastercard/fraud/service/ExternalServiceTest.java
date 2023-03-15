package com.mastercard.fraud.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mastercard.fraud.model.externalApi.CardUsageDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.mastercard.fraud.model.externalApi.CardUsagePO;

import java.math.BigInteger;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ExternalServiceTest {
    @Mock
    private ExternalService externalService;

    @Test
    void testSearchCardUsage() {
        // arrange
        BigInteger cardNum = new BigInteger("1234567890123456");
        Integer totalUsage = 0;
        CardUsagePO[] cardUsageList = new CardUsagePO[7];
        for (int i = 0; i < cardUsageList.length; i++) {
            cardUsageList[i] = new CardUsagePO(i+1);
            totalUsage += i+1;
        }

        CardUsageDto expectedCardUsage = CardUsageDto
                .builder()
                .weeklyUsage(cardUsageList)
                .totalUsage(totalUsage)
                .build();

        WebClient webClientMock = mock(WebClient.class);
        ResponseSpec responseSpecMock = mock(ResponseSpec.class);

        when(externalService.searchCardUsage(cardNum)).thenReturn(expectedCardUsage);

//        when(responseSpecMock.bodyToMono(CardUsagePO[].class)).thenReturn(
//                Mono.just(expectedCardUsage)
//        );

        // act
        CardUsageDto actualCardUsage = externalService.searchCardUsage(cardNum);

        log.info(actualCardUsage.toString());
        // assert
//        Assertions.assertArrayEquals(expectedCardUsage, actualCardUsage);
//        assertThat(actualCardUsage.length).isNotNull();;
    }




}