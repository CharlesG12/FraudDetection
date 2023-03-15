package com.mastercard.fraud.utils;

import com.mastercard.fraud.model.externalApi.CardUsageDto;
import com.mastercard.fraud.service.ExternalService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class externalDummyDataTest {

    @Resource
    ExternalService externalService;

    @Test
    public void dummyDataTest(){
        CardUsageDto cardUsageDummy = externalService.dummyData("dummy");
        log.info(cardUsageDummy.toString());

    }
}
