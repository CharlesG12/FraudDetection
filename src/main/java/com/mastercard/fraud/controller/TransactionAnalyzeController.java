package com.mastercard.fraud.controller;
import com.mastercard.fraud.model.InputValidationResponse;
import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.ResponseDTO;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.service.FraudDetectionService;
import com.mastercard.fraud.utils.AjaxResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analyzeTransaction")
@Slf4j

public class TransactionAnalyzeController {
    @Resource(name = "FraudDetectService")
    FraudDetectionService fraudDetectionService;

    @PostMapping
    @CrossOrigin(origins = "http://localhost:8080")
    public AjaxResponse analyzeTransaction(@RequestBody AnalyzeRequest analyzeRequest) {
        log.info("analyze transaction post request:" + analyzeRequest);

        InputValidationResponse inputValidationResponse = fraudDetectionService.validateInput(analyzeRequest);
        if(!inputValidationResponse.isValid()) {
            log.info(inputValidationResponse.getMessage());
            return AjaxResponse.fail(inputValidationResponse);
        }

        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);
        ResponseDTO responseDTO = ResponseDTO.builder().responses(responseList).build();
        return AjaxResponse.success(responseDTO);
    }
}
