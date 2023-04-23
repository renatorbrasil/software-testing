package com.amigoscode.testing.utils;

import org.springframework.util.StringUtils;

import java.util.function.Predicate;

public class PhoneNumberValidator implements Predicate<String> {
    @Override
    public boolean test(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            return false;
        }

        if (!phoneNumber.startsWith("+44")) {
            return false;
        }

        if (phoneNumber.length() != 14) {
            return false;
        }

        return true;
    }
}
