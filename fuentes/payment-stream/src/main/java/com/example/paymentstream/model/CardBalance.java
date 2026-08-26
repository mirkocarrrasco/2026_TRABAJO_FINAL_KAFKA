package com.example.paymentstream.model;

import java.math.BigDecimal;

public record CardBalance(
        String card_id,
        BigDecimal total
) {
}
