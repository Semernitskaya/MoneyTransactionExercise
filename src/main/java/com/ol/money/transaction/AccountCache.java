package com.ol.money.transaction;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
@Slf4j
public class AccountCache {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public boolean addAccount(String userName) {
        return addAccount(userName, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN));
    }

    public boolean addAccount(String userName, BigDecimal amount) {
        log.info("Trying to add account userName {}, amount {}", userName, amount);
        var existedAccount = accounts.putIfAbsent(userName, new Account(userName, amount));
        return existedAccount == null;
    }

    Map<String, Account> getAccounts() {
        return accounts;
    }

    public Optional<Map<String, Account>> acquireAccounts(String... userNames) {
        log.info("Trying to acquire accounts {}", userNames);
        var result = new HashMap<String, Account>(userNames.length);
        Arrays.sort(userNames);
        for (String userName : userNames) {
            Account account = accounts.get(userName);
            if (account == null) {
                log.debug("Account {} isn't found", userName);
                return Optional.empty();
            }
            if (!account.getLock().tryLock()) {
                log.debug("Account {} is already locked", userName);
                return Optional.empty();
            }
            result.put(userName, account);
        }
        return Optional.of(result);
    }

    public void resumeAccounts(String... userNames) {
        log.info("Trying to resume accounts {}", userNames);
        Arrays.sort(userNames, Comparator.reverseOrder());
        for (String userName : userNames) {
            Account account = accounts.get(userName);
            if (account == null) {
                log.debug("Account {} isn't found", userName);
                continue;
            }
            try {
                account.getLock().unlock();
            } catch (Exception e) {
//               e will be skipped in non-debug mode
                log.debug("Account {} wasn't locked", userName);
            }
        }
    }
}
