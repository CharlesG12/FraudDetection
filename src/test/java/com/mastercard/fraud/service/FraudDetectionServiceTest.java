package com.mastercard.fraud.service;

import com.mastercard.fraud.config.DecisionRuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class FraudDetectionServiceTest {
    private ExternalService externalService;
    private DecisionRuleConfig decisionRuleConfig;

    @BeforeAll
    public void setup() {
        
    }



}
