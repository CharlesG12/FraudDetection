package com.mastercard.fraud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Configuration
@ConfigurationProperties(prefix = "decision-rule")
public class DecisionRuleConfig {
    private BigDecimal transactionHardLimit;
    private BigDecimal transactionAvgLimit;
    private Integer usageHardLimit;
    private Integer usageSoftLimit;
}


