package van.karm.auction.presentation.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AuctionDatesValidator.class)
@Documented
public @interface ValidAuctionDates {
    String message() default "Invalid auction dates";

    int maxYearsInFuture() default 1;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
