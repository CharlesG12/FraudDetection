package com.mastercard.fraud.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.ResponseVO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(transactionList.getCardNum().size(), 1);
        assertEquals(transactionList.getCardNum().get(0).toString(), "5026840000000001");
        assertEquals(transactionList.getAmount().get(0).toString(), "3.99");
    }

    @Test
    public void nullRequestMappingTest_expectNull() throws IOException {
        AnalyzeRequest request = AnalyzeRequest.builder().build();

        TransactionList transactionList = transactionMapper.transactionPOList(request);
        assertEquals(transactionList.getCardNum(), null);
        assertEquals(transactionList.getAmount(), null);

        TransactionList transactionList_null = transactionMapper.transactionPOList(null);
        assertEquals(transactionList_null, null);

    }

    @Test
    public void nullResponseMappingTest_expectNull() throws IOException {
        Response request = Response.builder().build();

        ResponseVO responseVO = transactionMapper.responseVO(request);
        assertEquals(responseVO.getCardNumber(), null);
        assertEquals(responseVO.getIsApproved(), null);
        assertEquals(responseVO.getTransactionAmount(), null);
        assertEquals(responseVO.getWeeklyUseFrequency(), null);

        ResponseVO responseVO1_null = transactionMapper.responseVO((Response) null);
        assertEquals(responseVO1_null, null);

        List<ResponseVO> responseVOList_null = transactionMapper.responseVO((List<Response>) null);
        assertEquals(responseVOList_null, null);

    }

}
