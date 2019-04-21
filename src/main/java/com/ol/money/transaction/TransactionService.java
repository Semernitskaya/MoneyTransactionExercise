package com.ol.money.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    //    TODO: add DI
    private final AccountCache cache;

    public boolean transferMoney(String fromUserName, String toUserName, BigDecimal transferAmount) {
        log.info("Transferring {} from user {} to user {}", transferAmount, fromUserName, toUserName);
        try {
            var optionalAccounts = cache.acquireAccounts(fromUserName, toUserName);
            if (optionalAccounts.isEmpty()) {
                log.warn("Can't lock users");
                return false;
            }
            var accounts = optionalAccounts.get();
            var fromAccount = accounts.get(fromUserName);
            var toAccount = accounts.get(toUserName);
            if (fromAccount.getAmount().compareTo(transferAmount) < 0) {
                log.warn("Account {} has insufficient funds", fromAccount);
                return false;
            }
            fromAccount.setAmount(fromAccount.getAmount().subtract(transferAmount));
            toAccount.setAmount(toAccount.getAmount().add(transferAmount));
            return true;
        } finally {
            cache.resumeAccounts(fromUserName, toUserName);
        }
    }
}
