package com.mastercard.fraud.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.transactionPost.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoadJsonObjectTest {
    @Test
    void testJsonRequestPOJOConversion() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/sample.json")));
        AnalyzeRequest actual = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);

        System.out.println(actual);
    }

}
