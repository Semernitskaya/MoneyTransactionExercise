package com.ol.money.transaction.validation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public class UserNameValidatorTest {

    private UserNameValidator validator = new UserNameValidator();

    @DataProvider
    public Object[][] getTestData() {
        return new Object[][]{
                {null, false},
                {"", false},
                {"   ", true},
                {"test_name", true}
        };
    }

    @Test(dataProvider = "getTestData")
    public void testIsValid(String name, boolean expectedResult) {
        assertEquals(validator.isValid(name), expectedResult);
    }
}