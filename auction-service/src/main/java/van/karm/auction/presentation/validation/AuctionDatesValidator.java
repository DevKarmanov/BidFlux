package van.karm.auction.presentation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import van.karm.auction.presentation.dto.request.CreateAuction;

import java.time.LocalDateTime;

public class AuctionDatesValidator implements ConstraintValidator<ValidAuctionDates, CreateAuction> {
    private int maxYears;

    @Override
    public void initialize(ValidAuctionDates constraintAnnotation) {
        this.maxYears = constraintAnnotation.maxYearsInFuture();
    }

    @Override
    public boolean isValid(CreateAuction dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        LocalDateTime start = dto.getStartDate();
        LocalDateTime end = dto.getEndDate();

        if (start == null || end == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean valid = true;

        context.disableDefaultConstraintViolation();

        if (end.isBefore(start)) {
            context.buildConstraintViolationWithTemplate("End date cannot be before start date")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        }

        if (end.isEqual(start)) {
            context.buildConstraintViolationWithTemplate("Start and end dates cannot be the same")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        }

        if (start.isAfter(now.plusYears(maxYears)) || end.isAfter(now.plusYears(maxYears))) {
            context.buildConstraintViolationWithTemplate(
                            String.format("Auction dates cannot be more than %d year(s) in the future", maxYears)
                    )
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
