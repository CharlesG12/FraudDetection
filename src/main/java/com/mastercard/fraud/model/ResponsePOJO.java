package com.mastercard.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePOJO {
    private String CardNumber;
    private BigDecimal TransactionAmount;
    private String isApproved;
    private Integer weeklyUseFrequency;
}
