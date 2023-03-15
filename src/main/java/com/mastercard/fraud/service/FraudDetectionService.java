package com.mastercard.fraud.service;

import com.mastercard.fraud.config.DecisionRuleConfig;
import com.mastercard.fraud.model.*;
import com.mastercard.fraud.model.externalApi.CardUsageDto;
import com.mastercard.fraud.model.transactionPost.Amount;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.model.transactionPost.CardNum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
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
            if (!isInputValid(transactionPO, analyzeRequest)) {
                responseList.add(emptyResponse());
                continue;
            }

            BigInteger cardNumber = transactionPO.getCardNum();
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

    private boolean isInputValid(TransactionPO transactionPO, AnalyzeRequest analyzeRequest){
        BigInteger cardNumber = transactionPO.getCardNum();
        BigDecimal amount = transactionPO.getAmount();

        CardNum cardNumRequest = analyzeRequest.getPropertiesRoot().getTransaction().getPropertiesTransaction().getCardNum();
        Amount amountRqequest = analyzeRequest.getPropertiesRoot().getTransaction().getPropertiesTransaction().getAmount();

        if (cardNumber.toString().length() != 16){
            return false;
        }

        if( cardNumber.compareTo(cardNumRequest.getMaximum()) > 0) {
            return false;
        }

        if( cardNumber.compareTo(cardNumRequest.getMinimum()) < 0) {
            return false;
        }

        if( amount.compareTo(amountRqequest.getMinimum()) < 0) {
            return false;
        }

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
        List<BigInteger> cardNumberList = analyzeRequest
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
