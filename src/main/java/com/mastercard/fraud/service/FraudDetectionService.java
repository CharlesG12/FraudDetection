package com.mastercard.fraud.service;
import com.mastercard.fraud.config.DecisionRuleConfig;
import com.mastercard.fraud.model.*;
import com.mastercard.fraud.model.externalApi.CardUsageWeekly;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import com.mastercard.fraud.utils.TransactionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service( "FraudDetectService")
public class FraudDetectionService {
    @Resource
    ExternalService externalService;

    @Resource
    TransactionMapper mapper;

    @Resource
    DecisionRuleConfig decisionRuleConfig;

    public InputValidationResponse validateInput(AnalyzeRequest analyzeRequest) {
        TransactionList transactionList = mapper.transactionPOList(analyzeRequest);

        if(transactionList.getCardNum().size() != transactionList.getAmount().size()) {
            return InputValidationResponse.builder().isValid(false).message("Input amount count does not match card number count").build();
        }

        BigInteger maxCardNum = analyzeRequest.getPropertiesRoot().getTransaction().getPropertiesTransaction().getCardNum().getMaximum();
        BigInteger minCardNum = analyzeRequest.getPropertiesRoot().getTransaction().getPropertiesTransaction().getCardNum().getMinimum();
        BigDecimal minUsageAmount = analyzeRequest.getPropertiesRoot().getTransaction().getPropertiesTransaction().getAmount().getMinimum();

        boolean isValid = true;
        StringBuilder messageBuilder = new StringBuilder();

        for( BigInteger cardNum : transactionList.getCardNum()) {
            if( cardNum.compareTo(maxCardNum) > 0) {
                isValid = false;
                messageBuilder.append( String.format("Card number is over Max limit, Error Input: %s", cardNum));
            }

            if( cardNum.compareTo(minCardNum) < 0) {
                isValid = false;
                messageBuilder.append( String.format("Card number is under Min limit, Error Input: %s", cardNum));
            }
        }

        for( BigDecimal amount : transactionList.getAmount()) {
            if( amount.compareTo(minUsageAmount) < 0) {
                isValid = false;
                messageBuilder.append( String.format("Transaction amount is under min limit, Error Input: %s", amount));
            }
        }

        return InputValidationResponse.builder().isValid(isValid).message(messageBuilder.toString()).build();
    }

    public List<Response> validateTransaction(AnalyzeRequest analyzeRequest) {
        List<Response> responseList = new ArrayList<>();

        TransactionList transactionList = mapper.transactionPOList(analyzeRequest);
        int transactionCount = transactionList.getAmount().size();

        for (int i = 0; i < transactionCount; i++) {
            BigInteger cardNumber = transactionList.getCardNum().get(i);
            BigDecimal amount = transactionList.getAmount().get(i);

            CardUsageWeekly cardUsage = externalService.searchCardUsage(cardNumber);
            log.info("cardUsage " + cardUsage.toString());

            IsApproved isApproved = isTransactionApproved(amount, decisionRuleConfig, cardUsage);

            Response response = Response
                    .builder()
                    .CardNumber(cardNumber)
                    .TransactionAmount(amount)
                    .isApproved(isApproved.isApproved())
                    .weeklyUseFrequency(cardUsage.getTotalUsage())
                    .message(isApproved.getMessage())
                    .build();
            log.info(response.toString());
            responseList.add(response);
        }

        return responseList;
    }

    private IsApproved isTransactionApproved(BigDecimal amount, DecisionRuleConfig decisionRuleConfig, CardUsageWeekly cardUsageWeekly) {
        if( isTransOverLimit(amount, decisionRuleConfig)) {
            return IsApproved.builder().isApproved(false).message("Transaction amount is over max limit").build();
        }

        if( isCardOverused(cardUsageWeekly, decisionRuleConfig)) {
            return IsApproved.builder().isApproved(false).message("Usage is over max limit").build();
        }

        if( isOverAvgLimit(amount, cardUsageWeekly, decisionRuleConfig)) {
            return IsApproved.builder().isApproved(false).message("avergae spending is over limit").build();
        }
        return IsApproved.builder().isApproved(true).message("transaction approved").build();
    }

    private boolean isTransOverLimit(BigDecimal amount, DecisionRuleConfig decisionRuleConfig){
        return amount.compareTo(decisionRuleConfig.getTransactionHardLimit()) > 0;
    }

    private boolean isCardOverused(CardUsageWeekly cardUsageWeekly, DecisionRuleConfig decisionRuleConfig){
        return cardUsageWeekly.getTotalUsage() > decisionRuleConfig.getUsageHardLimit();
    }

    private boolean isOverAvgLimit(BigDecimal amount, CardUsageWeekly cardUsageWeekly, DecisionRuleConfig decisionRuleConfig) throws ArithmeticException{
        if (amount.compareTo(new BigDecimal("0.00")) == 0) {
            return false;
        }

        if(cardUsageWeekly.getTotalUsage() < decisionRuleConfig.getUsageSoftLimit()) {
            if(cardUsageWeekly.getTotalUsage() == 0 ) {
                return false;
            }
            BigDecimal avgSpend = amount.divide(BigDecimal.valueOf(cardUsageWeekly.getTotalUsage()), 3, RoundingMode.CEILING);
            if( avgSpend.compareTo(decisionRuleConfig.getTransactionAvgLimit()) > 0 ) {
                return true;
            }
        }
        return false;
    }
}