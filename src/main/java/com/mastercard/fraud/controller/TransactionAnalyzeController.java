package com.mastercard.fraud.controller;
import com.mastercard.fraud.exception.CustomException;
import com.mastercard.fraud.model.InputValidationResponse;
import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.ResponseDTO;
import com.mastercard.fraud.model.ResponseVO;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.service.FraudDetectionService;
import com.mastercard.fraud.exception.AjaxResponse;
import com.mastercard.fraud.utils.TransactionMapper;
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

    @Resource
    TransactionMapper mapper;

    @PostMapping
    @CrossOrigin(origins = "http://localhost:8080")
    public AjaxResponse analyzeTransaction(@RequestBody AnalyzeRequest analyzeRequest) {
        log.info("analyze transaction post request:" + analyzeRequest);

        fraudDetectionService.validateInput(analyzeRequest);

        List<Response> responseList = fraudDetectionService.validateTransaction(analyzeRequest);
        List<ResponseVO> responseVOList = mapper.responseVO(responseList);
        ResponseDTO responseDTO = ResponseDTO.builder().responses(responseVOList).build();
        return AjaxResponse.success(responseDTO);
    }
}
