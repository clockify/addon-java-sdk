package com.cake.clockify.addonsdk.shared.validators;

import com.cake.clockify.addonsdk.shared.ValidationException;
import com.cake.clockify.addonsdk.shared.model.Webhook;
import com.cake.clockify.addonsdk.shared.utils.ValidationUtils;

public class WebhookValidator implements Validator<Webhook> {
    @Override
    public void validate(Webhook obj) {
        if (!ValidationUtils.isValidManifestPath(obj.getPath())) {
            throw new ValidationException("Url should be an absolute path and not end with a slash.");
        }
    }
}
