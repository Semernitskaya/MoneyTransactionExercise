package com.ol.money.transaction.web;

import com.google.gson.Gson;
import com.ol.money.transaction.AccountCache;
import com.ol.money.transaction.AccountService;
import com.ol.money.transaction.TransactionService;
import com.ol.money.transaction.response.AccountResponse;
import com.ol.money.transaction.response.AccountStatus;
import com.ol.money.transaction.response.TransactionResponse;
import com.ol.money.transaction.response.TransactionStatus;

import java.util.Map;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static spark.Spark.*;

/**
 * Created by Semernitskaya on 13.04.2019.
 */
public class Main {

    private static AccountCache cache = new AccountCache();

    private static TransactionService transactionService = new TransactionService(cache);

    private static AccountService accountService = new AccountService(cache);

    public static void main(String[] args) {

        post("/add", (request, response) -> {
            Map parameters = new Gson().fromJson(request.body(), Map.class);
            response.type(JSON_UTF_8.toString());
            AccountStatus status = accountService.addAccount(parameters);
            return new Gson().toJson(AccountResponse.of(status));
        });


        put("/transfer", (request, response) -> {
            Map parameters = new Gson().fromJson(request.body(), Map.class);
            response.type(JSON_UTF_8.toString());
            TransactionStatus status = transactionService.transferMoney(parameters);
            return new Gson().toJson(TransactionResponse.of(status));
        });
    }
}
