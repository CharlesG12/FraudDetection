package com.mastercard.fraud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.exception.CustomException;
import com.mastercard.fraud.model.InputValidationResponse;
import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class FraudDetectionJsonFileTest {
    @Resource
    FraudDetectionService fraudDetectionService;

    @Test
    void swaggerDefaultJson_expectApproved() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/requestTestData/swaggerDefault.json");
        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);

        assertEquals(1, responseList.size());
        assertEquals("0", responseList.get(0).getCardNumber().toString());
        assertEquals("0", responseList.get(0).getTransactionAmount().toString());
        assertEquals(1, responseList.size());
        assertEquals("transaction approved", responseList.get(0).getMessage().toString());
    }

    @Test
    void negativeAmount_expectFail() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/requestTestData/negativeTransaction.json");

        CustomException customException = assertThrows(CustomException.class,
                () -> fraudDetectionService.validateInput(analyzeRequest));

        assertEquals("transaction amount is smaller than the minimum requirement", customException.getMessage());
        assertEquals(400, customException.getCode());
    }

    public AnalyzeRequest getJsonRequestTestData(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get(path)));
        AnalyzeRequest analyzeRequest = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);
        return analyzeRequest;
    }

}