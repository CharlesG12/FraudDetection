package com.mastercard.fraud.controller;

import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.ResponseDTO;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.service.FraudDetectionService;
import com.mastercard.fraud.utils.AjaxResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analyzeTransaction")
@Slf4j

public class TransactionAnalyzeController {

    @Resource(name = "FraudDetectService")
    FraudDetectionService fraudDetectionService;

    @GetMapping("/test")
    public AjaxResponse hello() {
        return AjaxResponse.success();
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:8080")
    public AjaxResponse analyzeTransaction(@RequestBody AnalyzeRequest analyzeRequest) {
        log.info("analyze transaction post request:" + analyzeRequest);
        ResponseDTO responseDTO = fraudDetectionService.validateTransaction(analyzeRequest);
        return AjaxResponse.success(responseDTO);
    }


}
