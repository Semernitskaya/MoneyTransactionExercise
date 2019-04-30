package com.ol.money.transaction.response;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public enum TransactionStatus {

    OK,
    INVALID_USER_NAME,
    INVALID_TRANSFER_AMOUNT,
    CANT_LOCK_USERS,
    CANT_TRANSFER_TO_SAME_ACCOUNT,
    INSUFFICIENT_FUNDS,
    OTHER_ERROR


}
