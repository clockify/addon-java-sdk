package com.cake.clockify.addonsdk.shared.validators;

import com.cake.clockify.addonsdk.shared.ValidationException;
import com.cake.clockify.addonsdk.shared.model.Component;
import com.cake.clockify.addonsdk.shared.utils.ValidationUtils;

public class ComponentValidator implements Validator<Component> {
    @Override
    public void validate(Component obj) {
        if (!ValidationUtils.isValidManifestPath(obj.getPath())) {
            throw new ValidationException("Path should be an absolute path and not end with a slash.");
        }
    }
}
