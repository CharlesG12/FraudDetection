package com.mastercard.fraud.service;

import com.mastercard.fraud.config.ExternalApiConfig;
import com.mastercard.fraud.model.externalApi.CardUsageDto;
import com.mastercard.fraud.model.externalApi.CardUsagePO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

@Slf4j
@Service("ExternalService")
public class ExternalService {
    @Resource
    ExternalApiConfig externalApiConfig;

    @Resource
    public WebClient webClient;

    @CrossOrigin(origins = "http://www.randomnumberapi.com")
    public CardUsageDto searchCardUsage(BigInteger cardNum) {
        CardUsagePO[] cardUsage_list = new CardUsagePO[7];
        try {
            cardUsage_list = webClient
                    .get()
                    .uri(externalApiConfig.getUrl())
                    .retrieve()
                    .bodyToMono(CardUsagePO[].class)
                    .block();
        } catch (Exception  ex){
            log.error("external api no response" + ex.getMessage());

            return dummyData("this is a random generated data");
        }

        Integer totalUsage = Arrays.stream(cardUsage_list).map(CardUsagePO::getUsage).reduce(0, Integer::sum);

        CardUsageDto cardUsageDto = CardUsageDto
                .builder()
                .weeklyUsage(cardUsage_list)
                .totalUsage(totalUsage)
                .message("ok")
                .build();

        log.info("external api request:" + cardNum.toString() + " " + cardUsageDto);

        return cardUsageDto;
    }

    // populate dummy data when external service is down
    public CardUsageDto dummyData(String message) {
        String cardNum = "1234567890123456";
        Integer totalUsage = 0;
        Random rd = new Random();
        CardUsagePO[] cardUsageList = new CardUsagePO[7];

        for (int i = 0; i < cardUsageList.length; i++) {
            Integer count = rd.nextInt(0, 20);
            cardUsageList[i] = new CardUsagePO(count);
            totalUsage += count;
        }

        CardUsageDto cardUsage = CardUsageDto
                .builder()
                .weeklyUsage(cardUsageList)
                .totalUsage(totalUsage)
                .message(message)
                .build();

        return cardUsage;
    }

}
