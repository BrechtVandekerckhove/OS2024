package validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = StartConstraintValidator.class)
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ValidStart {

	String message()

	default "{validator.invalidStartNull}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	// De datum moet liggen tussen 26 juli 2024 en 11 augustus 2024. Aanvanguur
	// vanaf 8 uur
	LocalDate minDate = LocalDate.of(2024, 7, 26);
	LocalDate maxDate = LocalDate.of(2024, 8, 11);
	int minHour = 8;
}