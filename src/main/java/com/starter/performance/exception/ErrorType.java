package com.starter.performance.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    EMAIL_IS_DUPLICATED_EXCEPTION("EMAIL_IS_DUPLICATED_EXCEPTION"),
    NICKNAME_IS_DUPLICATED_EXCEPTION("NICKNAME_IS_DUPLICATED_EXCEPTION"),
    UNSUBSCRIBED_EMAIL_EXCEPTION("UNSUBSCRIBED_EMAIL_EXCEPTION"),
    WRONG_PASSWORD_EXCEPTION("WRONG_PASSWORD_EXCEPTION");

    private final String errorType;
}