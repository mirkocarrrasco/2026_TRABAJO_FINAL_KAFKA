package com.example.payment.model;

import java.math.BigDecimal;

public record Payment(
        Long timestamp,
        String card_id,
        BigDecimal amount,
        String type
) {
}
