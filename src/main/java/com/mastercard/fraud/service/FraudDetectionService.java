package com.mastercard.fraud.service;

import com.mastercard.fraud.config.DecisionRuleConfig;
import com.mastercard.fraud.model.*;
import com.mastercard.fraud.model.externalApi.CardUsageDto;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service( "FraudDetectService")
public class FraudDetectionService {
    @Resource
    ExternalService externalService;

    @Resource
    DecisionRuleConfig decisionRuleConfig;

    public ResponseDTO validateTransaction(AnalyzeRequest analyzeRequest) {

        List<TransactionPO> transactionPOList = extractTransactionData(analyzeRequest);
        List<Response> responseList = new ArrayList<>();

        for (TransactionPO transactionPO : transactionPOList) {
            if (!isInputValid(transactionPO)) {
                responseList.add(emptyResponse());
                continue;
            }

            String cardNumber = transactionPO.getCardNum();
            BigDecimal amount = transactionPO.getAmount();

            CardUsageDto cardUsage = externalService.searchCardUsage(cardNumber);
            log.info("cardUsage " + cardUsage.toString());

            Boolean isApproved = isTransactionApproved(transactionPO, decisionRuleConfig, cardUsage);

            Response response = Response
                    .builder()
                    .CardNumber(cardNumber)
                    .TransactionAmount(amount)
                    .isApproved(isApproved)
                    .weeklyUseFrequency(cardUsage.getTotalUsage())
                    .message("ok")
                    .build();
            responseList.add(response);
        }

        ResponseDTO responseDTO = ResponseDTO.builder().responses(responseList).build();
        return responseDTO;
    }

    private boolean isTransactionApproved(TransactionPO transactionPO, DecisionRuleConfig decisionRuleConfig, CardUsageDto cardUsageDto) {
        // TODO: input error handling
        // TODO: Validate input values

        if( isTransOverLimit(transactionPO, decisionRuleConfig)) {
            return true;
        }

        if( isCardOverused(cardUsageDto, decisionRuleConfig)) {
            return true;
        }

        if( isOverAvgLimit(transactionPO, cardUsageDto, decisionRuleConfig)) {
            return true;
        }
        return false;
    }

    private boolean isTransOverLimit(TransactionPO transactionPO, DecisionRuleConfig decisionRuleConfig){
        if(transactionPO.getAmount().compareTo(decisionRuleConfig.getTransactionHardLimit()) > 0) {
            return true;
        }
        return false;
    }

    private boolean isCardOverused(CardUsageDto cardUsageDto, DecisionRuleConfig decisionRuleConfig){
        if(cardUsageDto.getTotalUsage() > decisionRuleConfig.getUsageHardLimit()) {
            return true;
        }
        return false;
    }

    private boolean isOverAvgLimit(TransactionPO transactionPO, CardUsageDto cardUsageDto, DecisionRuleConfig decisionRuleConfig){
        if(cardUsageDto.getTotalUsage() < decisionRuleConfig.getUsageSoftLimit()) {
            BigDecimal avgSpend = transactionPO.getAmount().divide(BigDecimal.valueOf(cardUsageDto.getTotalUsage()));
            if( avgSpend.compareTo(decisionRuleConfig.getTransactionAvgLimit()) > 0 ) {
                return true;
            }
        }
        return false;
    }

    private boolean isInputValid(TransactionPO transactionPO){
        String cardNumber = transactionPO.getCardNum();
        BigDecimal amount = transactionPO.getAmount();

        if (cardNumber.length() != 16){
            return false;
        }

        String regex = "\\d+";
        if(!cardNumber.matches(regex)) {
            return false;
        }

//        Transaction might be negative, for example: refund
//        if (amount.compareTo(BigDecimal.ZERO) < 0) {
//            return false;
//        }

        return true;
    }

    private Response emptyResponse() {
        Response response = Response
                .builder()
                .isApproved(false)
                .message("Invalid input")
                .build();
        return response;
    };

    private List<TransactionPO> extractTransactionData(AnalyzeRequest analyzeRequest){
        List<TransactionPO> transactionPOList = new ArrayList<>();
        List<String> cardNumberList = analyzeRequest
                .getPropertiesRoot()
                .getTransaction()
                .getPropertiesTransaction()
                .getCardNum()
                .getExamples();

        List<BigDecimal> amountList = analyzeRequest
                .getPropertiesRoot()
                .getTransaction()
                .getPropertiesTransaction()
                .getAmount()
                .getExamples();

        for(int i = 0; i < cardNumberList.size(); i++) {
            TransactionPO transactionPO = TransactionPO
                    .builder()
                    .cardNum(cardNumberList.get(i))
                    .amount(amountList.get(i))
                    .build();
            transactionPOList.add(transactionPO);
        }

        return transactionPOList;
    }
}
