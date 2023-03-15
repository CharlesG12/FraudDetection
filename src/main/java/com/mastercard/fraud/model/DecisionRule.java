package com.mastercard.fraud.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "decision-rule")
public class DecisionRule {
    private BigDecimal transactionHardLimit;
    private BigDecimal transactionAvgLimit;
    private Integer usageHardLimit;
    private Integer usageSoftLimit;
}


