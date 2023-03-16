package com.mastercard.fraud.utils;

import com.mastercard.fraud.config.DecisionRuleConfig;
import com.mastercard.fraud.config.ExternalApiConfig;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ValueBindTest {
    @Resource
    DecisionRuleConfig decisionRuleConfig;

    @Resource
    ExternalApiConfig externalApiConfig;

    @Test
    public void decisionRuleValueBindTests() throws Exception {
        System.out.println(decisionRuleConfig.toString());
    }

    @Test
    public void externalApiValueBindTests() throws Exception {
        System.out.println(externalApiConfig.toString());
    }

}