package com.mastercard.fraud.model.transactionPost;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String title;
    private String type;
    private List<String> required;
    @JsonProperty("properties")
    private PropertiesTransaction propertiesTransaction;
}
