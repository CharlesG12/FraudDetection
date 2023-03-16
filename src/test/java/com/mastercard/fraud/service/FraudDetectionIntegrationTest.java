package com.mastercard.fraud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.config.DecisionRuleConfig;
import com.mastercard.fraud.model.InputValidationResponse;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.utils.TransactionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.xmlunit.builder.Input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class FraudDetectionIntegrationTest {
    @Resource
    FraudDetectionService fraudDetectionService;

    @Test
    void test() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/requestTestData/swaggerDefault.json");
        InputValidationResponse inputValidationResponse = fraudDetectionService.validateInput(analyzeRequest);

        log.info(inputValidationResponse.toString());
    }

    @Test
    void negativeAmount_expectFail() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/requestTestData/negativeTransaction.json");
        InputValidationResponse inputValidationResponse = fraudDetectionService.validateInput(analyzeRequest);

        log.info(inputValidationResponse.toString());
    }

    public AnalyzeRequest getJsonRequestTestData(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get(path)));
        AnalyzeRequest analyzeRequest = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);
        return analyzeRequest;
    }

}
