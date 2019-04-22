package com.ol.money.transaction.validation;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
@Slf4j
public class AmountParser {
    public Optional<BigDecimal> getAmount(String amountStr) {
        try {
            BigDecimal amount = new BigDecimal(amountStr)
                    .setScale(2, RoundingMode.HALF_DOWN);
            return Optional.of(amount);
        } catch (Exception e) {
            log.warn("Error while parsing amount value", e);
            return Optional.empty();
        }
    }
}
