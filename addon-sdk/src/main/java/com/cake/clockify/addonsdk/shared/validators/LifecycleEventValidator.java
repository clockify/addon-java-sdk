package com.cake.clockify.addonsdk.shared.validators;

import com.cake.clockify.addonsdk.shared.ValidationException;
import com.cake.clockify.addonsdk.shared.model.LifecycleEvent;
import com.cake.clockify.addonsdk.shared.utils.ValidationUtils;

public class LifecycleEventValidator implements Validator<LifecycleEvent> {
    @Override
    public void validate(LifecycleEvent obj) {
        if (!ValidationUtils.isValidManifestPath(obj.getPath())) {
            throw new ValidationException("Url should be an absolute path and not end with a slash.");
        }
    }
}
