package com.mastercard.fraud.service;
import com.mastercard.fraud.config.ExternalApiConfig;
import com.mastercard.fraud.exception.CustomException;
import com.mastercard.fraud.exception.CustomExceptionType;
import com.mastercard.fraud.model.externalApi.CardUsageWeekly;
import com.mastercard.fraud.model.externalApi.CardUsage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigInteger;
import java.time.Duration;
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
    public CardUsageWeekly searchCardUsage(BigInteger cardNum) {
        String url = externalApiConfig.getUrl();
        CardUsage[] cardUsage_list;
        try {
            Mono<CardUsage[]> webGetResponse = webClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(CardUsage[].class)
                    .timeout(Duration.ofMillis(10000));
//                    .block();
            cardUsage_list = webGetResponse.block();
        } catch (Exception  ex){
            log.error("external api no response" + ex.getMessage());
            return dummyData("external api failed, random generate weekly usage data");
//            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, "External Api failed");
        }

        Integer totalUsage = Arrays.stream(cardUsage_list).map(CardUsage::getUsage).reduce(0, Integer::sum);

        CardUsageWeekly cardUsageWeekly = CardUsageWeekly
                .builder()
                .weeklyUsage(cardUsage_list)
                .totalUsage(totalUsage)
                .message("ok")
                .build();

        log.info("external api request:" + cardNum.toString() + " " + cardUsageWeekly);

        return cardUsageWeekly;
    }

    // populate dummy data when external service is down
    public CardUsageWeekly dummyData(String message) {
        String cardNum = "1234567890123456";
        Integer totalUsage = 0;
        Random rd = new Random();
        CardUsage[] cardUsageList = new CardUsage[7];

        for (int i = 0; i < cardUsageList.length; i++) {
            Integer count = rd.nextInt(0, 5);
            cardUsageList[i] = new CardUsage(count);
            totalUsage += count;
        }

        CardUsageWeekly cardUsage = CardUsageWeekly
                .builder()
                .weeklyUsage(cardUsageList)
                .totalUsage(totalUsage)
                .message(message)
                .build();

        return cardUsage;
    }

}