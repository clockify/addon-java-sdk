package addonsdk.shared.validators;

import addonsdk.shared.ValidationException;
import addonsdk.shared.model.Webhook;
import addonsdk.shared.utils.ValidationUtils;

public class WebhookValidator implements Validator<Webhook> {
    @Override
    public void validate(Webhook obj) {
        if (!ValidationUtils.isValidManifestPath(obj.getPath())) {
            throw new ValidationException("Url should be an absolute path and not end with a slash.");
        }
    }
}
