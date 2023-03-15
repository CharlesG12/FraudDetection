package com.mastercard.fraud.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.request.*;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonTest {
    @Test
    void testJsonRequestPOJOConversion() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/sample.json")));
        RequestPOJO actual = mapper.readValue(sampleJsonRequest, RequestPOJO.class);

        System.out.println(actual);
    }

}
