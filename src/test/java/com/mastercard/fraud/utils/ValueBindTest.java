package com.mastercard.fraud.utils;

import com.mastercard.fraud.model.DecisionRule;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ValueBindTest {
    @Resource
    DecisionRule decisionRule;

    @Test
    public void valueBindTests() throws Exception {
        System.out.println(decisionRule.toString());
    }

}