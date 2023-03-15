package com.mastercard.fraud.model;

import lombok.Data;

import java.util.List;

@Data
public class weeklyPurchaseAmount {
    private List<Integer> count;
}
