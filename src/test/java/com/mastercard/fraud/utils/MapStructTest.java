package com.mastercard.fraud.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.TransactionList;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class MapStructtest {

    @Resource
    TransactionMapper transactionMapper;

    @Test
    public void test() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/sample.json")));
        AnalyzeRequest request = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);

        TransactionList transactionList = transactionMapper.transactionPOList(request);
        log.info(String.valueOf(transactionList));
    }

}
