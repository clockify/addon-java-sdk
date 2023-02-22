package addonsdk.shared.validators;

import addonsdk.shared.ValidationException;

public interface Validator<T> {
    /**
     * @param obj
     * @throws ValidationException
     */
    void validate(T obj);
}
