package com.ol.money.transaction.validation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.testng.Assert.assertEquals;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public class AmountParserTest {

    private AmountParser parser = new AmountParser();

    @DataProvider
    public Object[][] getTestData() {
        return new Object[][]{
                {null, Optional.empty()},
                {"", Optional.empty()},
                {"invalid_amount", Optional.empty()},
                {"5", Optional.of(BigDecimal.valueOf(5.00).setScale(2))},
                {"5.12", Optional.of(BigDecimal.valueOf(5.12))},
                {"5.121", Optional.of(BigDecimal.valueOf(5.12))},
                {"5.125", Optional.of(BigDecimal.valueOf(5.12))},
                {"5.1287", Optional.of(BigDecimal.valueOf(5.13))},
        };
    }

    @Test(dataProvider = "getTestData")
    public void testGetAmount(String amountStr, Optional<BigDecimal> expected) {
        assertEquals(parser.getAmount(amountStr), expected);
    }
}