package com.mastercard.fraud.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SampleDataProvider {
    public AnalyzeRequest getJsonRequest(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get(path)));
        AnalyzeRequest analyzeRequest = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);
        return analyzeRequest;
    }
}
