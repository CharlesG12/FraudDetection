package com.mastercard.fraud.model.transactionPost;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertiesRoot {
    @JsonProperty("transaction")
    private Transaction transaction;
}
