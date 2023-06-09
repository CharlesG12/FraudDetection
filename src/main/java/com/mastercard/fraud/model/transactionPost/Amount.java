package com.mastercard.fraud.model.transactionPost;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Amount {
    private String title;
    private String type;
    private BigDecimal minimum;
    private List<BigDecimal> examples;
    @JsonProperty("default")
    private BigDecimal defaultAmount;

}
