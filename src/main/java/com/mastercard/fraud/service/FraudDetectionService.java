package com.mastercard.fraud.service;

import com.mastercard.fraud.model.*;
import com.mastercard.fraud.model.externalApi.CardUsageDto;
import com.mastercard.fraud.model.transactionPost.RequestDto;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service( "FraudDetectService")
public class FraudDetectionService {
    private static final int global_usageLimit = 60;
    private static final int global_usageCheckpoint = 35;
    private static final BigDecimal global_avgSpendLimit = new BigDecimal(500);
    private static final BigDecimal global_spendLimit = new BigDecimal(50000);

    @Resource
    ExternalService externalService;

    @Resource
    DecisionRule decisionRule;

    public ResponseDTO validateTransaction(RequestDto requestDto) {
    //TODO: Add map struct

        // examples is a list, we take the first one
        String cardNumber = requestDto
                .getPropertiesRootPOJO()
                .getTransactionPOJO()
                .getPropertiesTransactionPOJO()
                .getCardNumPOJO()
                .getExamples()
                .get(0).toString();

        // examples is a list, we take the first one
        BigDecimal amount = requestDto
                .getPropertiesRootPOJO()
                .getTransactionPOJO()
                .getPropertiesTransactionPOJO()
                .getAmountPOJO()
                .getExamples()
                .get(0);

        TransactionPO transactionPO = TransactionPO
                .builder()
                .cardNum(cardNumber)
                .amount(amount)
                .build();

        CardUsageDto cardUsage = externalService.searchCardUsage(cardNumber);
        log.info("cardUsage" + cardUsage.toString());

        Boolean isApproved = isTransactionApproved(transactionPO, decisionRule, cardUsage);

        ResponseDTO requestPOJOP = ResponseDTO
                .builder()
                .CardNumber(cardNumber)
                .TransactionAmount(amount)
                .isApproved(isApproved)
                .weeklyUseFrequency(cardUsage.getTotalUsage())
                .build();

        return requestPOJOP;
    }

    private boolean isTransactionApproved(TransactionPO transactionPO, DecisionRule decisionRule, CardUsageDto cardUsageDto) {
        // TODO: input error handling
        // TODO: Validate input values

        if( isTransOverLimit(transactionPO, decisionRule)) {
            return true;
        }

        if( isCardOverused(cardUsageDto, decisionRule)) {
            return true;
        }

        if( isOverAvgLimit(transactionPO, cardUsageDto, decisionRule)) {
            return true;
        }
        return false;
    }

    private boolean isTransOverLimit(TransactionPO transactionPO, DecisionRule decisionRule){
        if(transactionPO.getAmount().compareTo(decisionRule.getTransactionHardLimit()) > 0) {
            return true;
        }
        return false;
    }

    private boolean isCardOverused(CardUsageDto cardUsageDto, DecisionRule decisionRule){
        if(cardUsageDto.getTotalUsage() > decisionRule.getUsageHardLimit()) {
            return true;
        }
        return false;
    }

    private boolean isOverAvgLimit(TransactionPO transactionPO, CardUsageDto cardUsageDto, DecisionRule decisionRule){
        if(cardUsageDto.getTotalUsage() < decisionRule.getUsageSoftLimit()) {
            BigDecimal avgSpend = transactionPO.getAmount().divide(BigDecimal.valueOf(cardUsageDto.getTotalUsage()));
            if( avgSpend.compareTo(decisionRule.getTransactionAvgLimit()) > 0 ) {
                return true;
            }
        }
        return false;
    }

}
