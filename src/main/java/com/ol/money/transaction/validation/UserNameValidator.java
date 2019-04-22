package com.ol.money.transaction.validation;

import com.google.common.base.Strings;

/**
 * Created by Semernitskaya on 22.04.2019.
 */
public class UserNameValidator {

    public boolean isValid(String userName) {
        return !Strings.isNullOrEmpty(userName);
    }
}
