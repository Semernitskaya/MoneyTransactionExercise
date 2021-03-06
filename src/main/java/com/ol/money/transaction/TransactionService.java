package com.ol.money.transaction;

import com.ol.money.transaction.response.TransactionStatus;
import com.ol.money.transaction.validation.AmountParser;
import com.ol.money.transaction.validation.UserNameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.ol.money.transaction.response.TransactionStatus.*;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    public static final String FROM_KEY = "from";
    public static final String TO_KEY = "to";
    public static final String AMOUNT_KEY = "amount";

    private final AccountCache cache;

    private final UserNameValidator nameValidator = new UserNameValidator();

    private final AmountParser amountParser = new AmountParser();

    public TransactionStatus transferMoney(Map<String, String> parameters) {
        var fromUserName = parameters.get(FROM_KEY);
        var toUserName = parameters.get(TO_KEY);
        var transferAmountStr = parameters.get(AMOUNT_KEY);
        log.info("Transferring {} from user {} to user {}", transferAmountStr, fromUserName, toUserName);
        if (!nameValidator.isValid(fromUserName) || !nameValidator.isValid(toUserName)) {
            return INVALID_USER_NAME;
        }
        if (fromUserName.equals(toUserName)) {
            return CANT_TRANSFER_TO_SAME_ACCOUNT;
        }
        var optionalTransferAmount = amountParser.getAmount(transferAmountStr);
        if (optionalTransferAmount.isEmpty()) {
            return INVALID_TRANSFER_AMOUNT;
        }
        var transferAccount = optionalTransferAmount.get();
        try {
            var optionalAccounts = cache.acquireAccounts(fromUserName, toUserName);
            if (optionalAccounts.isEmpty()) {
                log.warn("Can't lock users");
                return CANT_LOCK_USERS;
            }
            var accounts = optionalAccounts.get();
            var fromAccount = accounts.get(fromUserName);
            var toAccount = accounts.get(toUserName);
            if (fromAccount.getAmount().compareTo(transferAccount) < 0) {
                log.warn("Account {} has insufficient funds", fromAccount);
                return INSUFFICIENT_FUNDS;
            }
            fromAccount.setAmount(fromAccount.getAmount().subtract(transferAccount));
            toAccount.setAmount(toAccount.getAmount().add(transferAccount));
            return OK;
        } finally {
            cache.resumeAccounts(fromUserName, toUserName);
        }
    }
}
