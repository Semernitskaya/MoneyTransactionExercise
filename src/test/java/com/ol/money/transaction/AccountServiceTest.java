package com.ol.money.transaction;

import com.ol.money.transaction.response.AccountStatus;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static com.ol.money.transaction.AccountService.AMOUNT_KEY;
import static com.ol.money.transaction.AccountService.NAME_KEY;
import static com.ol.money.transaction.response.AccountStatus.*;
import static java.util.Map.of;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public class AccountServiceTest {

    public static final String DUPLICATE_USER_NAME = "duplicate_user";
    @Mock
    private AccountCache cache;

    private AccountService service;

    @BeforeClass
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        service = new AccountService(cache);
        when(cache.addAccount(anyString())).thenReturn(true);
        when(cache.addAccount(argThat(argument -> argument.equals(DUPLICATE_USER_NAME)))).thenReturn(false);
        when(cache.addAccount(anyString(), any())).thenReturn(true);
    }

    @DataProvider
    public Object[][] getTestData() {
        return new Object[][]{
                {of(NAME_KEY, "", AMOUNT_KEY, "5"), INVALID_USER_NAME},
                {of(AMOUNT_KEY, "5"), INVALID_USER_NAME},
                {of(), INVALID_USER_NAME},
                {of(NAME_KEY, "user_1", AMOUNT_KEY, "5"), OK},
                {of(NAME_KEY, "user_1"), OK},
                {of(NAME_KEY, DUPLICATE_USER_NAME), AccountStatus.DUPLICATE_USER_NAME},
                {of(NAME_KEY, "user_1", AMOUNT_KEY, "invalid_amount"), INVALID_AMOUNT},
                {of(NAME_KEY, "user_1", AMOUNT_KEY, "5.0"), OK},
        };
    }

    @Test(dataProvider = "getTestData")
    public void testAddAccount(Map<String, String> parameters, AccountStatus expectedStatus) {
        assertEquals(service.addAccount(parameters), expectedStatus);
    }


}