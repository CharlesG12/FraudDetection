package com.mastercard.fraud.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.config.DecisionRuleConfig;
import com.mastercard.fraud.exception.CustomException;
import com.mastercard.fraud.model.InputValidationResponse;
import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.TransactionList;
import com.mastercard.fraud.model.externalApi.CardUsage;
import com.mastercard.fraud.model.externalApi.CardUsageWeekly;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.utils.TransactionMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.undo.CannotUndoException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class FraudDetectionServiceTest {
    @Mock
    private ExternalService externalServiceMock;

    @Mock
    private TransactionMapper mapperMock;

    @Mock
    private DecisionRuleConfig decisionRuleConfigMock;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    @Test
    public void cardNumError_expectInvalidResponse() throws IOException {
        // Create sample request data
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("10000000000000001"));
        cardNumList.add(new BigInteger("999999999999999"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("50001.00"));
        amountList.add(new BigDecimal("50001.00"));

        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);

        CustomException customException = assertThrows(CustomException.class,
                () -> fraudDetectionService.validateInput(analyzeRequest));

        assertEquals("card number is bigger than max card num", customException.getMessage());
        assertEquals(400, customException.getCode());
    }

    @Test
    public void requestCountNotMatch_expectInvalidResponse() throws IOException {
        // Create sample request data
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("5026840000000001"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("50001.00"));
        amountList.add(new BigDecimal("6050.00"));

        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);

        CustomException customException = assertThrows(CustomException.class,
                () -> fraudDetectionService.validateInput(analyzeRequest));

        assertEquals("Input amount count does not match card number count", customException.getMessage());
        assertEquals(400, customException.getCode());
    }

    @Test
    public void transactionOverLimit_expectInvalidResponse() throws IOException {
        BigDecimal transactionHardLimit = new BigDecimal("50000");
        // Create sample request data
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/requestTestData/overTransactionHardLimit.json");
        CardUsageWeekly cardUsageWeekly = getCardUsageWeeklTestData("src/test/resources/cardUsageWeeklyTestData/validUsage.json");

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("5026840000000001"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("50001.00"));

        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        when(decisionRuleConfigMock.getTransactionHardLimit()).thenReturn(transactionHardLimit);
        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);
        when(externalServiceMock.searchCardUsage(any(BigInteger.class))).thenReturn(cardUsageWeekly);

        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);

        BigDecimal expectedTransactionAmount = new BigDecimal("50001.00");
        String expectedMessage = "Transaction amount is over max limit";

        log.info(responseList.get(0).toString());
        assertEquals(1, responseList.size());
        assertEquals(expectedTransactionAmount, responseList.get(0).getTransactionAmount());
        assertEquals(false, responseList.get(0).getIsApproved());
        assertEquals(expectedMessage, responseList.get(0).getMessage());
    }

    @Test
    public void zeroAmount_expectApproved() throws IOException {
        // Create sample request data
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/requestTestData/overTransactionHardLimit.json");

        BigDecimal transactionHardLimit = new BigDecimal("50000");
        Integer usageHardLimit = 65;

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("5026840000000001"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("0.00"));
        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        // Create sample external api data
        CardUsage[] cardUsageList = new CardUsage[7];
        for(int i = 0; i < cardUsageList.length; i++){
            cardUsageList[i] = new CardUsage(1);
        }
        CardUsageWeekly cardUsageWeekly = CardUsageWeekly.builder().weeklyUsage(cardUsageList).totalUsage(1).message("ok").build();

        when(decisionRuleConfigMock.getTransactionHardLimit()).thenReturn(transactionHardLimit);
        when(decisionRuleConfigMock.getUsageHardLimit()).thenReturn(usageHardLimit);

        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);
        when(externalServiceMock.searchCardUsage(any(BigInteger.class))).thenReturn(cardUsageWeekly);

        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);

        BigDecimal expectedTransactionAmount = new BigDecimal("0.00");
        String expectedMessage = "transaction approved";

        log.info(responseList.get(0).toString());
        assertEquals(1, responseList.size());
        assertEquals(expectedTransactionAmount, responseList.get(0).getTransactionAmount());
        assertEquals(true, responseList.get(0).getIsApproved());
        assertEquals(expectedMessage, responseList.get(0).getMessage());
    }

    @Test
    public void usageOverLimit_expectInvalidResponse() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");
        CardUsageWeekly cardUsageWeekly = getCardUsageWeeklTestData("src/test/resources/cardUsageWeeklyTestData/usageOverHardLimit.json");

        BigDecimal transactionHardLimit = new BigDecimal("50000");
        Integer usageHardLimit = 65;

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("5026840000000001"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("33.00"));
        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        // Set up mockito behavior
        when(externalServiceMock.searchCardUsage(any(BigInteger.class))).thenReturn(cardUsageWeekly);
        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);
        when(decisionRuleConfigMock.getTransactionHardLimit()).thenReturn(transactionHardLimit);
        when(decisionRuleConfigMock.getUsageHardLimit()).thenReturn(usageHardLimit);

        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);

        BigDecimal expectedTransactionAmount = new BigDecimal("33.00");
        String expectedMessage = "Usage is over max limit";
        Integer expectedUsage = 70;

        log.info(responseList.get(0).toString());
        assertEquals(1, responseList.size());
        assertEquals(expectedTransactionAmount, responseList.get(0).getTransactionAmount());
        assertEquals(false, responseList.get(0).getIsApproved());
        assertEquals(expectedUsage, responseList.get(0).getWeeklyUseFrequency());
        assertEquals(expectedMessage, responseList.get(0).getMessage());
    }

    @Test
    public void transactionOverAvgLimit_expectInvalidResponse() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");

        BigDecimal transactionHardLimit = new BigDecimal("50000");
        BigDecimal transactionAvgLimit = new BigDecimal("500");
        Integer usageHardLimit = 65;
        Integer usageSoftLimit = 35;

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("5026840000000001"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("10000.00"));

        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        // Create sample external api data
        CardUsage[] cardUsageList = new CardUsage[7];
        for(int i = 0; i < cardUsageList.length; i++){
            cardUsageList[i] = new CardUsage(1);
        }
        CardUsageWeekly cardUsageWeekly = CardUsageWeekly.builder().weeklyUsage(cardUsageList).totalUsage(2).message("ok").build();

        // Set up mockito behavior
        when(externalServiceMock.searchCardUsage(any(BigInteger.class))).thenReturn(cardUsageWeekly);

        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);

        when(decisionRuleConfigMock.getTransactionHardLimit()).thenReturn(transactionHardLimit);
        when(decisionRuleConfigMock.getTransactionAvgLimit()).thenReturn(transactionAvgLimit);
        when(decisionRuleConfigMock.getUsageHardLimit()).thenReturn(usageHardLimit);
        when(decisionRuleConfigMock.getUsageSoftLimit()).thenReturn(usageSoftLimit);

        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);

        BigDecimal expectedTransactionAmount = new BigDecimal("10000.00");
        String expectedMessage = "avergae spending is over limit";
        Integer expectedUsage = 2;

        assertEquals(1, responseList.size());
        assertEquals(expectedTransactionAmount, responseList.get(0).getTransactionAmount());
        assertEquals(false, responseList.get(0).getIsApproved());
        assertEquals(expectedUsage, responseList.get(0).getWeeklyUseFrequency());
        assertEquals(expectedMessage, responseList.get(0).getMessage());
    }

    @Test
    public void zeroUsage_expectApproved() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");

        BigDecimal transactionHardLimit = new BigDecimal("50000");
        BigDecimal transactionAvgLimit = new BigDecimal("500");
        Integer usageHardLimit = 65;
        Integer usageSoftLimit = 35;

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("5026840000000001"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("10000.00"));

        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        // Create sample external api data
        CardUsage[] cardUsageList = new CardUsage[7];
        for(int i = 0; i < cardUsageList.length; i++){
            cardUsageList[i] = new CardUsage(1);
        }
        CardUsageWeekly cardUsageWeekly = CardUsageWeekly.builder().weeklyUsage(cardUsageList).totalUsage(0).message("ok").build();

        // Set up mockito behavior
        when(externalServiceMock.searchCardUsage(any(BigInteger.class))).thenReturn(cardUsageWeekly);

        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);

        when(decisionRuleConfigMock.getTransactionHardLimit()).thenReturn(transactionHardLimit);
//        when(decisionRuleConfigMock.getTransactionAvgLimit()).thenReturn(transactionAvgLimit);
        when(decisionRuleConfigMock.getUsageHardLimit()).thenReturn(usageHardLimit);
        when(decisionRuleConfigMock.getUsageSoftLimit()).thenReturn(usageSoftLimit);

        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);

        BigDecimal expectedTransactionAmount = new BigDecimal("10000.00");
        String expectedMessage = "transaction approved";
        Integer expectedUsage = 0;

        assertEquals(1, responseList.size());
        assertEquals(expectedTransactionAmount, responseList.get(0).getTransactionAmount());
        assertEquals(true, responseList.get(0).getIsApproved());
        assertEquals(expectedUsage, responseList.get(0).getWeeklyUseFrequency());
        assertEquals(expectedMessage, responseList.get(0).getMessage());
    }

    @Test
    public void transactionO_expectValidResponse() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");

        BigDecimal transactionHardLimit = new BigDecimal("50000");
        BigDecimal transactionAvgLimit = new BigDecimal("500");
        Integer usageHardLimit = 65;
        Integer usageSoftLimit = 35;

        // Create sample request data
        List<BigInteger> cardNumList = new ArrayList<>();
        cardNumList.add(new BigInteger("5026840000000001"));
        cardNumList.add(new BigInteger("5026840000000001"));
        List<BigDecimal> amountList = new ArrayList<>();
        amountList.add(new BigDecimal("9000.00"));
        amountList.add(new BigDecimal("800.00"));

        TransactionList transactionList = TransactionList.builder().cardNum(cardNumList).amount(amountList).build();

        // Create sample external api data
        CardUsage[] cardUsageList = new CardUsage[7];
        for(int i = 0; i < cardUsageList.length; i++){
            cardUsageList[i] = new CardUsage(1);
        }
        CardUsageWeekly cardUsageWeekly = CardUsageWeekly.builder().weeklyUsage(cardUsageList).totalUsage(2).message("ok").build();

        // Set up mockito behavior
        when(externalServiceMock.searchCardUsage(any(BigInteger.class))).thenReturn(cardUsageWeekly);

        when(mapperMock.transactionPOList(any(AnalyzeRequest.class))).thenReturn(transactionList);

        when(decisionRuleConfigMock.getTransactionHardLimit()).thenReturn(transactionHardLimit);
        when(decisionRuleConfigMock.getTransactionAvgLimit()).thenReturn(transactionAvgLimit);
        when(decisionRuleConfigMock.getUsageHardLimit()).thenReturn(usageHardLimit);
        when(decisionRuleConfigMock.getUsageSoftLimit()).thenReturn(usageSoftLimit);


        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);


        BigDecimal expectedTransactionAmount = new BigDecimal("9000.00");
        String expectedMessage = "avergae spending is over limit";
        Integer expectedUsage = 2;

        assertEquals(2, responseList.size());
        assertEquals(expectedTransactionAmount, responseList.get(0).getTransactionAmount());
        assertEquals(false, responseList.get(0).getIsApproved());
        assertEquals(expectedUsage, responseList.get(0).getWeeklyUseFrequency());
        assertEquals(expectedMessage, responseList.get(0).getMessage());
    }



    @Test
    public void testValidateTransaction() throws IOException {
        // Create sample request data
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/requestTestData/validTransaction.json");

        // Set up sample external service response
        CardUsageWeekly cardUsageWeekly = getCardUsageWeeklTestData("src/test/resources/cardUsageWeeklyTestData/usageOverHardLimit.json");

        log.info(cardUsageWeekly.toString());

    }

    public AnalyzeRequest getJsonRequestTestData(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get(path)));
        AnalyzeRequest analyzeRequest = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);
        return analyzeRequest;
    }

    public CardUsageWeekly getCardUsageWeeklTestData(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJson = new String(Files.readAllBytes(Paths.get(path)));
        CardUsageWeekly cardUsageWeekly = mapper.readValue(sampleJson, CardUsageWeekly.class);
        return cardUsageWeekly;
    }
}