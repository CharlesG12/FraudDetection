package com.mastercard.fraud.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class controllerIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper mapper;

    @Test
    public void analyzeTransaction_shouldReturnSuccess() throws Exception {
        // Arrange
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/sample.json")));
        AnalyzeRequest actual = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .request(HttpMethod.POST, "/analyzeTransaction")
                                .contentType("application/json")
                                .content(sampleJsonRequest))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        log.info(contentAsString);
    }

    @Test
    public void analyzeTransaction_shouldReturnDecline() throws Exception {
        // Arrange
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requestTestData/overTransactionHardLimit.json")));
        AnalyzeRequest actual = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .request(HttpMethod.POST, "/analyzeTransaction")
                                .contentType("application/json")
                                .content(sampleJsonRequest))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        log.info(contentAsString);
    }

    @Test
    public void analyzeTransaction_shouldReturnInvalidInput() throws Exception {
        // Arrange
        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requestTestData/cardNumberError.json")));
        AnalyzeRequest actual = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);

        // Act
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .request(HttpMethod.POST, "/analyzeTransaction")
                                .contentType("application/json")
                                .content(sampleJsonRequest))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"code\":400,\"message\":\"card number is bigger than max card num\",\"data\":null,\"ok\":false}";

        assertEquals(expected, contentAsString);
    }

    @Test
    public void postBadRequest_expectException() throws Exception {
        // Arrange
        String sampleJsonRequest = "Broken Json";
        String expectedResponseBody = "{\"code\":999,\"message\":\"JSON parse error: Unrecognized token 'Broken': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\",\"data\":null,\"ok\":false}";

        mockMvc.perform( MockMvcRequestBuilders
                                .request(HttpMethod.POST, "/analyzeTransaction")
                                .contentType("application/json")
                                .content(sampleJsonRequest))
                .andExpect(result -> assertEquals(expectedResponseBody, result.getResponse().getContentAsString()));
    }

//    @Test
//    public void postBadRequest_expectException() throws Exception {
//        // Arrange
//        String sampleJsonRequest = new String(Files.readAllBytes(Paths.get("src/test/resources/requestTestData/cardNumberError.json")));
//        AnalyzeRequest actual = mapper.readValue(sampleJsonRequest, AnalyzeRequest.class);
//        String expectedResponseBody = "{\"code\":999,\"message\":\"JSON parse error: Unrecognized token 'Broken': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\",\"data\":null,\"ok\":false}";
//
//        mockMvc.perform( MockMvcRequestBuilders
//                        .request(HttpMethod.POST, "/analyzeTransaction")
//                        .contentType("application/json")
//                        .content(sampleJsonRequest))
//                .andExpect(result -> assertEquals(expectedResponseBody, result.getResponse().getContentAsString()));
//    }
}