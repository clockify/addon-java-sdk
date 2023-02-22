package addonsdk.shared.validators;

import addonsdk.shared.ValidationException;
import addonsdk.shared.model.Component;
import addonsdk.shared.utils.ValidationUtils;

public class ComponentValidator implements Validator<Component> {
    @Override
    public void validate(Component obj) {
        if (!ValidationUtils.isValidManifestPath(obj.getPath())) {
            throw new ValidationException("Path should be an absolute path and not end with a slash.");
        }
    }
}
