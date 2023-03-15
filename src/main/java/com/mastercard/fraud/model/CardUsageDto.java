package com.mastercard.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardUsageDto {
    private CardUsagePO[] weeklyUsage;
    private Integer totalUsage;

    private String message;
}
