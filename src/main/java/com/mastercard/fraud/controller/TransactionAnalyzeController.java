package com.mastercard.fraud.controller;

import com.mastercard.fraud.model.ResponseDTO;
import com.mastercard.fraud.model.request.RequestDto;
import com.mastercard.fraud.service.FraudDetectionService;
import com.mastercard.fraud.utils.AjaxResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public AjaxResponse analyzeTransaction(@RequestBody RequestDto requestDto) {
        log.info("analyze transaction post request:" + requestDto);
        ResponseDTO responseDto = fraudDetectionService.validateTransaction(requestDto);
        return AjaxResponse.success(responseDto);
    }


}
