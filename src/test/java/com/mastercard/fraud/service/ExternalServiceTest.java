package com.mastercard.fraud.service;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.mastercard.fraud.config.ExternalApiConfig;
import com.mastercard.fraud.model.externalApi.CardUsageWeekly;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import com.mastercard.fraud.model.externalApi.CardUsage;
import reactor.core.publisher.Mono;
import java.math.BigInteger;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ExternalServiceTest {
    @InjectMocks
    private ExternalService externalService;

    @Mock
    private WebClient webClientMock;

    @Mock
    private ExternalApiConfig externalApiConfigMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;


    @SuppressWarnings("unchecked")
    @Test
    public void test() {

        CardUsage[] postList  = new CardUsage[7];
        Integer[] validNums = new Integer[]{1, 2, 3, 4, 5, 6, 7};
        Integer sum = 0;
        for (int i = 0; i < validNums.length; i++) {
            CardUsage usage = CardUsage.builder().Usage(validNums[i]).build();
            postList[i] = usage;
            sum += validNums[i];
        }

        when(externalApiConfigMock.getUrl()).thenReturn("testUrl");
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(
                ArgumentMatchers.<Class<CardUsage[]>>notNull())).thenReturn(Mono.just(postList));

        CardUsageWeekly response = externalService.searchCardUsage(BigInteger.ONE);
        Assertions.assertEquals("ok", response.getMessage());
        Assertions.assertEquals(sum, response.getTotalUsage());
        Assertions.assertEquals(postList, response.getWeeklyUsage());
    }
}