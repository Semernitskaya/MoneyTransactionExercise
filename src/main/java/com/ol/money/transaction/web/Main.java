package com.ol.money.transaction.web;

import com.ol.money.transaction.AccountCache;
import com.ol.money.transaction.TransactionService;

import static spark.Spark.*;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
public class Main {

    private static AccountCache cache = new AccountCache();

    private static TransactionService service = new TransactionService(cache);

    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
