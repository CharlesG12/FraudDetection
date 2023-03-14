package com.mastercard.fraud.controller;

import com.mastercard.fraud.model.request.RequestPOJO;
import com.mastercard.fraud.utils.AjaxResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analyzeTransaction")
@Slf4j
public class TransactionAnalyzeController {

    @GetMapping("/test")
    public AjaxResponse hello() {
        return AjaxResponse.success();
    }

    @PostMapping
    public AjaxResponse analyzeTransaction(@RequestBody RequestPOJO requestPOJO) {
        log.info("analyze transaction post request:" + requestPOJO);
        return AjaxResponse.success();
    }

}
