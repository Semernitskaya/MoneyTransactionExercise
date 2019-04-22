package com.ol.money.transaction.response;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public enum TransactionStatus {

    OK,
    INVALID_USER_NAME,
    INVALID_TRANSFER_AMOUNT,
    CANT_LOCK_USERS,
    INSUFFICIENT_FUNDS,
    OTHER_ERROR


}
