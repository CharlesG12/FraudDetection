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
public class PropertiesRootPOJO {
    @JsonProperty("transaction")
    private TransactionPOJO transactionPOJO;
}
