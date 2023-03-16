package com.mastercard.fraud.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.InputValidationResponse;
import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.ResponseVO;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.service.FraudDetectionService;
import com.mastercard.fraud.utils.AjaxResponse;
import com.mastercard.fraud.utils.TransactionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class controllerUnitTest {
    @InjectMocks
    private TransactionAnalyzeController controller;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private TransactionMapper mapperMock;

    @Test
    public void expectInvalidInput() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");

        InputValidationResponse inputValidationResponse = InputValidationResponse.builder().isValid(false).message("test failed").build();

        when(fraudDetectionService.validateInput(any(AnalyzeRequest.class))).thenReturn(inputValidationResponse);

        AjaxResponse ajaxResponse = controller.analyzeTransaction(analyzeRequest);
        log.info(ajaxResponse.toString());

        Boolean expectedIsOk = true;
        Integer expectedCode = 200;
        String expectedMessage = "Invalid Input";

        assertEquals(expectedIsOk, ajaxResponse.isOk());
        assertEquals(expectedCode, ajaxResponse.getCode());
        assertEquals(expectedMessage, ajaxResponse.getMessage());
        log.info(ajaxResponse.getData().toString());
    }

    @Test
    public void expectValidInput() throws IOException {
        AnalyzeRequest analyzeRequest = getJsonRequestTestData("src/test/resources/sample.json");
        InputValidationResponse inputValidationResponse = InputValidationResponse.builder().isValid(true).message("valid input").build();

        List<Response> expectResponseList = new ArrayList<>();
        Response response = Response.builder()
                .CardNumber(new BigInteger("80804883759845"))
                .TransactionAmount(new BigDecimal("345.60"))
                .weeklyUseFrequency(30)
                .isApproved(true)
                .message("good day")
                .build();
        expectResponseList.add(response);

        List<ResponseVO> expectResponseVOList = new ArrayList<>();
        ResponseVO responseVO = ResponseVO.builder()
                .CardNumber(new BigInteger("80804883759845"))
                .TransactionAmount(new BigDecimal("345.60"))
                .weeklyUseFrequency(30)
                .isApproved(true)
                .build();
        expectResponseVOList.add(responseVO);

        when(fraudDetectionService.validateInput(any(AnalyzeRequest.class))).thenReturn(inputValidationResponse);
        when(fraudDetectionService.validateTransaction(any(AnalyzeRequest.class))).thenReturn(expectResponseList);
        when(mapperMock.responseVO(Mockito.<Response>anyList())).thenReturn(expectResponseVOList);

        AjaxResponse ajaxResponse = controller.analyzeTransaction(analyzeRequest);
        log.info(ajaxResponse.toString());

        Boolean expectedIsOk = true;
        Integer expectedCode = 200;
        String expectedMessage = "Get Successes";

        assertEquals(expectedIsOk, ajaxResponse.isOk());
        assertEquals(expectedCode, ajaxResponse.getCode());
        assertEquals(expectedMessage, ajaxResponse.getMessage());
        log.info(ajaxResponse.getData().toString());
    }

    public AnalyzeRequest getJsonRequestTestData(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get(path)));
        AnalyzeRequest analyzeRequest = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);
        return analyzeRequest;
    }

}
