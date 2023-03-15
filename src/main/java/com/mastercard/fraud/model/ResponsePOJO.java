package com.mastercard.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    private String CardNumber;
    private BigDecimal TransactionAmount;
    private Boolean isApproved;
    private Integer weeklyUseFrequency;
}
