package com.mastercard.fraud.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmountPOJO {
    private String title;
    private String type;
    private BigDecimal minimum;
    private List<BigDecimal> examples;
    @JsonProperty("default")
    private BigDecimal defaultAmount;

}
