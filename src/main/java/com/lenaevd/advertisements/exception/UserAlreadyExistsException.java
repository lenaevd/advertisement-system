package com.lenaevd.advertisements.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String nonUniqueField;

    public UserAlreadyExistsException(String nonUniqueFieldName, String value) {
        super("User with " + nonUniqueFieldName + " = " + value + " already exists");
        this.nonUniqueField = nonUniqueFieldName;
    }
}
