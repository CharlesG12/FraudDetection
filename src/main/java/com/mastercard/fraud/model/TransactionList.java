package com.mastercard.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionList {
    List<BigInteger> cardNum;
    List<BigDecimal> amount;
}
