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
public class Response {
    private BigInteger CardNumber;
    private BigDecimal TransactionAmount;
    private Boolean isApproved;
    private Integer weeklyUseFrequency;
    private String message;
}
