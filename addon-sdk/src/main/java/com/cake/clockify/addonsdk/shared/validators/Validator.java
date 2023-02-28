package com.cake.clockify.addonsdk.shared.validators;

import com.cake.clockify.addonsdk.shared.ValidationException;

public interface Validator<T> {
    /**
     * @param obj
     * @throws ValidationException
     */
    void validate(T obj);
}
