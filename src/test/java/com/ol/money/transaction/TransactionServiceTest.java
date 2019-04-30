package com.ol.money.transaction;

import com.ol.money.transaction.response.TransactionStatus;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static com.ol.money.transaction.AccountService.AMOUNT_KEY;
import static com.ol.money.transaction.TransactionService.FROM_KEY;
import static com.ol.money.transaction.TransactionService.TO_KEY;
import static com.ol.money.transaction.response.TransactionStatus.*;
import static java.util.Map.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public class TransactionServiceTest {

    public static final String LOCKED_USER_NAME = "locked_user";
    public static final String MISSING_USER_NAME = "missing_user";
    public static final String FROM_USER_NAME = "user_from";
    public static final String TO_USER_NAME = "user_to";

    private Account accountFrom;

    private Account accountTo;

    @Mock
    private AccountCache cache;

    private TransactionService service;

    @BeforeClass
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        service = new TransactionService(cache);
    }


    @BeforeMethod
    public void prepareMethod() {
        accountFrom = new Account(FROM_USER_NAME, BigDecimal.valueOf(100).setScale(2));
        accountTo = new Account(TO_USER_NAME, BigDecimal.valueOf(20).setScale(2));
        when(cache.acquireAccounts(any())).thenAnswer(invocation -> {
            if (invocation.getArguments().length >= 2 &&
                    (MISSING_USER_NAME.equals(invocation.getArgument(0))
                        || LOCKED_USER_NAME.equals(invocation.getArgument(0))
                        || MISSING_USER_NAME.equals(invocation.getArgument(1))
                        || LOCKED_USER_NAME.equals(invocation.getArgument(1)))) {
                return Optional.empty();
            } else {
                return Optional.of(Map.of(FROM_USER_NAME, accountFrom, TO_USER_NAME, accountTo));
            }
        });
    }

    @DataProvider
    public Object[][] getTestData() {
        return new Object[][]{
                {
                        of(
                                FROM_KEY, FROM_USER_NAME,
                                TO_KEY, TO_USER_NAME,
                                AMOUNT_KEY, "10.0"),
                        OK,
                        BigDecimal.valueOf(90).setScale(2),
                        BigDecimal.valueOf(30).setScale(2),
                },
                {
                        of(
                                FROM_KEY, FROM_USER_NAME,
                                TO_KEY, TO_USER_NAME,
                                AMOUNT_KEY, "invalid_amount"),
                        INVALID_TRANSFER_AMOUNT,
                        BigDecimal.valueOf(100).setScale(2),
                        BigDecimal.valueOf(20).setScale(2),
                },
                {
                        of(
                                FROM_KEY, LOCKED_USER_NAME,
                                TO_KEY, TO_USER_NAME,
                                AMOUNT_KEY, "10.0"),
                        CANT_LOCK_USERS,
                        BigDecimal.valueOf(100).setScale(2),
                        BigDecimal.valueOf(20).setScale(2),
                },
                {
                        of(
                                FROM_KEY, FROM_USER_NAME,
                                TO_KEY, MISSING_USER_NAME,
                                AMOUNT_KEY, "10.0"),
                        CANT_LOCK_USERS,
                        BigDecimal.valueOf(100).setScale(2),
                        BigDecimal.valueOf(20).setScale(2),
                },
                {
                        of(
                                FROM_KEY, FROM_USER_NAME,
                                TO_KEY, FROM_USER_NAME,
                                AMOUNT_KEY, "10.0"),
                        CANT_TRANSFER_TO_SAME_ACCOUNT,
                        BigDecimal.valueOf(100).setScale(2),
                        BigDecimal.valueOf(20).setScale(2),
                },
                {
                        of(
                                FROM_KEY, "",
                                TO_KEY, TO_USER_NAME,
                                AMOUNT_KEY, "10.0"),
                        INVALID_USER_NAME,
                        BigDecimal.valueOf(100).setScale(2),
                        BigDecimal.valueOf(20).setScale(2),
                },
                {
                        of(
                                FROM_KEY, FROM_USER_NAME,
                                TO_KEY, "",
                                AMOUNT_KEY, "10.0"),
                        INVALID_USER_NAME,
                        BigDecimal.valueOf(100).setScale(2),
                        BigDecimal.valueOf(20).setScale(2),
                },
                {
                        of(
                                FROM_KEY, FROM_USER_NAME,
                                TO_KEY, TO_USER_NAME,
                                AMOUNT_KEY, "110.0"),
                        INSUFFICIENT_FUNDS,
                        BigDecimal.valueOf(100).setScale(2),
                        BigDecimal.valueOf(20).setScale(2),
                },

        };
    }

    @Test(dataProvider = "getTestData")
    public void testTransferMoney(Map<String, String> parameters,
                                  TransactionStatus expectedStatus,
                                  BigDecimal expectedFromAmount,
                                  BigDecimal expectedToAmount) {
        assertEquals(service.transferMoney(parameters), expectedStatus);
        assertEquals(accountFrom.getAmount(), expectedFromAmount);
        assertEquals(accountTo.getAmount(), expectedToAmount);
    }

}