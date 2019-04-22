package com.ol.money.transaction.response;

import lombok.RequiredArgsConstructor;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
@RequiredArgsConstructor(staticName = "of")
public class TransactionResponse {

    private final TransactionStatus status;

    private final String message;

    public static TransactionResponse of(TransactionStatus status) {
        return of(status, "");
    }
}
