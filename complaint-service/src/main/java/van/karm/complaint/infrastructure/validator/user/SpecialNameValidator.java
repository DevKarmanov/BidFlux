package van.karm.complaint.infrastructure.validator.user;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("special-name-validator")
public class SpecialNameValidator implements Validator {
    @Override
    public void validate(String input) {
        if (input != null && input.toLowerCase().contains("deleted".toLowerCase())) {
            throw new EntityNotFoundException("It is not possible to provide information on this request");
        }
    }
}
