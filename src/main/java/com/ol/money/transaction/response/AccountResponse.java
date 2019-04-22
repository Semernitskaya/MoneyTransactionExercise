package com.ol.money.transaction.response;

import lombok.RequiredArgsConstructor;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
@RequiredArgsConstructor(staticName = "of")
public class AccountResponse {

    private final AccountStatus status;

    private final String message;

    public static AccountResponse of(AccountStatus status) {
        return of(status, "");
    }
}
