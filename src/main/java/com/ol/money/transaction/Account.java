package com.ol.money.transaction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "amount,lock")
public class Account {

//    TODO: add possibility for negative amount
    private final String userName;

    private final Lock lock = new ReentrantLock();

    private BigDecimal amount = BigDecimal.ZERO;

    public Account(String userName, BigDecimal amount) {
        this.userName = userName;
        this.amount = amount;
    }
}
