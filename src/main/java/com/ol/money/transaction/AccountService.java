package com.ol.money.transaction;

import com.ol.money.transaction.response.AccountStatus;
import com.ol.money.transaction.validation.AmountParser;
import com.ol.money.transaction.validation.UserNameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.ol.money.transaction.response.AccountStatus.*;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountCache cache;

    private final AmountParser amountParser = new AmountParser();

    private final UserNameValidator nameValidator = new UserNameValidator();

    public AccountStatus addAccount(Map<String, String> parameters) {
        var userName = parameters.get("name");
        if (!nameValidator.isValid(userName)) {
            return INVALID_USER_NAME;
        }
        var amountStr = parameters.get("amount");
        if (amountStr == null) {
            return cache.addAccount(userName) ? OK : DUPLICATE_USER_NAME;
        }
        var amount = amountParser.getAmount(amountStr);
        return amount.map(val -> cache.addAccount(userName, val) ? OK : DUPLICATE_USER_NAME)
                .orElse(AccountStatus.INVALID_AMOUNT);

    }
}
