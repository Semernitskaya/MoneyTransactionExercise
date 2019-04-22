package com.ol.money.transaction;

import lombok.SneakyThrows;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public class AccountCacheTest {

    public static final String USER_NAME_1 = "user_1";
    public static final String USER_NAME_2 = "user_2";
    public static final String USER_NAME_3 = "user_3";
    public static final String USER_NAME_4 = "user_4";

    @Test
    public void testAddAccount() {
        var cache = new AccountCache();
        assertTrue(cache.addAccount(USER_NAME_1));
        assertTrue(cache.addAccount(USER_NAME_2));
        assertAccounts(cache, 0.00);
    }

    @Test
    public void testAddDuplicateAccount() {
        var cache = new AccountCache();
        assertTrue(cache.addAccount(USER_NAME_1));
        assertFalse(cache.addAccount(USER_NAME_1));
        assertTrue(cache.addAccount(USER_NAME_2));
        assertFalse(cache.addAccount(USER_NAME_2));
        assertAccounts(cache, 0.00);
    }

    @Test
    public void testAddAccountWithAmount() {
        var cache = new AccountCache();
        assertTrue(cache.addAccount(USER_NAME_1, BigDecimal.TEN.setScale(2)));
        assertTrue(cache.addAccount(USER_NAME_2, BigDecimal.TEN.setScale(2)));
        assertAccounts(cache, 10.00);
    }

    @Test
    public void testAcquireAccountsSuccessfully() {
        var cache = createTestCache();
        Optional<Map<String, Account>> optionalAccounts = cache.acquireAccounts(USER_NAME_1, USER_NAME_3);
        assertTrue(optionalAccounts.isPresent());
        assertThat(optionalAccounts.get().keySet()).containsExactlyInAnyOrder(USER_NAME_1, USER_NAME_3);
        assertThat(optionalAccounts.get().values()).allMatch(this::isLocked);

    }

    @Test
    public void testAcquireAccountsMultipleTimes() {
        var cache = createTestCache();
        cache.acquireAccounts(USER_NAME_1, USER_NAME_3);
        Optional<Map<String, Account>> optionalAccounts = cache.acquireAccounts(USER_NAME_1, USER_NAME_2);
        assertTrue(optionalAccounts.isPresent());
        assertThat(optionalAccounts.get().keySet()).containsExactlyInAnyOrder(USER_NAME_1, USER_NAME_2);
        optionalAccounts.get().forEach((key, account) -> {
            if (USER_NAME_1.equals(key)) {
                assertEquals(((ReentrantLock) account.getLock()).getHoldCount(), 2);
            } else {
                assertEquals(((ReentrantLock) account.getLock()).getHoldCount(), 1);
            }
        });
    }

    @Test
    public void testAcquireAccountsNotFound() {
        var cache = createTestCache();
        Optional<Map<String, Account>> optionalAccounts = cache.acquireAccounts(USER_NAME_1, USER_NAME_4);
        assertTrue(optionalAccounts.isEmpty());
    }

    @Test
    @SneakyThrows
    public void testAcquireAccountsLocked() {
        var cache = createTestCache();
        cache.acquireAccounts(USER_NAME_1);
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            Optional<Map<String, Account>> optionalAccounts = cache.acquireAccounts(USER_NAME_1, USER_NAME_2);
            assertTrue(optionalAccounts.isEmpty());
            latch.countDown();
        }).start();
        latch.await(5, TimeUnit.SECONDS);
    }


    @Test
    @SneakyThrows
    public void testAcquireAccountsMultipleThreads() {
        var cache = createTestCache();
        Collection<Account> accounts = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            accounts.addAll(cache.acquireAccounts(USER_NAME_1, USER_NAME_2).get().values());
            latch.countDown();
        }).start();
        new Thread(() -> {
            accounts.addAll(cache.acquireAccounts(USER_NAME_3).get().values());
            latch.countDown();
        }).start();
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(accounts.size(), 3);
        assertThat(accounts).allMatch(this::isLocked);
    }

    @Test
    public void testResumeAccounts() {
        var cache = createTestCache();
        cache.acquireAccounts(USER_NAME_1, USER_NAME_2);
        cache.resumeAccounts(USER_NAME_2, USER_NAME_1, USER_NAME_4);
        assertThat(cache.getAccounts().values()).noneMatch(this::isLocked);
    }

    private void assertAccounts(AccountCache cache, double amount) {
        var accounts = cache.getAccounts();
        assertEquals(accounts.size(), 2);
        accounts.forEach((key, account) -> {
            assertEquals(account.getAmount(), BigDecimal.valueOf(amount).setScale(2));
            assertEquals(key, account.getUserName());
            assertFalse(isLocked(account));
        });
    }

    private boolean isLocked(Account account) {
        return ((ReentrantLock) account.getLock()).isLocked();
    }

    private AccountCache createTestCache() {
        var cache = new AccountCache();
        assertTrue(cache.addAccount(USER_NAME_1, BigDecimal.TEN.setScale(2)));
        assertTrue(cache.addAccount(USER_NAME_2, BigDecimal.TEN.setScale(2)));
        assertTrue(cache.addAccount(USER_NAME_3, BigDecimal.TEN.setScale(2)));
        return cache;
    }
}