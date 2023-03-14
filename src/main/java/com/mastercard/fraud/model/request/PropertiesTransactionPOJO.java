package com.mastercard.fraud.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertiesTransactionPOJO {
    @JsonProperty("cardNum")
    private CardNumPOJO cardNumPOJO;

    @JsonProperty("amount")
    private AmountPOJO amountPOJO;
}
