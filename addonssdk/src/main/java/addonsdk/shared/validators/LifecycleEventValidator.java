package addonsdk.shared.validators;

import addonsdk.shared.ValidationException;
import addonsdk.shared.model.LifecycleEvent;
import addonsdk.shared.utils.ValidationUtils;

public class LifecycleEventValidator implements Validator<LifecycleEvent> {
    @Override
    public void validate(LifecycleEvent obj) {
        if (!ValidationUtils.isValidManifestPath(obj.getPath())) {
            throw new ValidationException("Url should be an absolute path and not end with a slash.");
        }
    }
}
