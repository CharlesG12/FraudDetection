package com.mastercard.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseVO {
    private BigInteger CardNumber;
    private BigDecimal TransactionAmount;
    private Boolean isApproved;
    private Integer weeklyUseFrequency;
    @Override
    public String toString() {
//        BigInteger var10000 = this.getCardNumber();
        String cardNum = this.getCardNumber().toString();
        StringBuilder builder = new StringBuilder(cardNum);
        builder.replace(4, 12, "********");

        return "Response(CardNumber=" + builder.toString() + ", TransactionAmount=" + this.getTransactionAmount() + ", isApproved=" + this.getIsApproved() + ", weeklyUseFrequency=" + this.getWeeklyUseFrequency() + ")";
    }

}
