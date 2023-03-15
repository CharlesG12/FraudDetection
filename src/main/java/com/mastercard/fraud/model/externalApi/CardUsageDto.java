package com.mastercard.fraud.model.externalApi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardUsageDto {
    private CardUsagePO[] weeklyUsage;
    private Integer totalUsage;

    private String message;
}
