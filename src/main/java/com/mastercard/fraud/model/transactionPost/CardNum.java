package com.mastercard.fraud.model.transactionPost;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardNum {
    private String title;
    private String type;
    private BigInteger minimum;
    private BigInteger maximum;
    private List<BigInteger> examples;
    @JsonProperty("default")
    private BigInteger defaultAmount;

}
