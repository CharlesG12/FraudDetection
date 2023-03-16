
package com.mastercard.fraud.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.Response;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ObfuscateTest {

    @Test
    void obfuscateCardNumTest(){
        String expect = "Response(CardNumber=9999********9999, TransactionAmount=803.3400000000000318323145620524883270263671875, isApproved=true, weeklyUseFrequency=40, message=test)";
        Response actual = Response.builder()
                .CardNumber(new BigInteger("9999999999999999"))
                .isApproved(true)
                .weeklyUseFrequency(40)
                .message("test")
                .TransactionAmount(new BigDecimal(803.34))
                .build();
        log.info(actual.toString());
        assertEquals(expect, actual.toString());
    }

    @Test
    void expectObfuscateRequestCarNumTest() throws IOException {
        String expect = "AnalyzeRequest(title=root, type=object, required=[transaction], propertiesRoot=PropertiesRoot(transaction=Transaction(title=transaction, type=object, required=[cardNum, amount], propertiesTransaction=PropertiesTransaction(cardNum=CardNum(title=cardnum, type=integer, minimum=1000000000000000, maximum=9999999999999999, examples=5026********0001, defaultAmount=9999999999999999), amount=Amount(title=amount, type=number, minimum=0.00, examples=[3.99], defaultAmount=0.00)))))";
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/sample.json")));
        AnalyzeRequest actual = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);
        log.info(actual.toString());
        assertEquals(expect, actual.toString());
    }
}
