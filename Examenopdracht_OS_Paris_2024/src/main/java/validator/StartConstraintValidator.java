package validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StartConstraintValidator implements ConstraintValidator<ValidStart, LocalDateTime> {
	private LocalDate minDate, maxDate;
	private int minHour;
	private String datePattern = "dd/MM/YYYY";

	@Override
	public void initialize(ValidStart constraintAnnotation) {
		minDate = ValidStart.minDate;
		maxDate = ValidStart.maxDate;
		minHour = ValidStart.minHour;
	}

	// De datum moet liggen tussen 26 juli 2024 en 11 augustus 2024. Aanvanguur
	// vanaf 8 uur
	@Override
	public boolean isValid(LocalDateTime ldt, ConstraintValidatorContext context) {
		if (ldt == null) {
			return false;
		}

		LocalDate date = ldt.toLocalDate();
		int hour = ldt.getHour();

		boolean isValid = ((date.isEqual(minDate) || date.isAfter(minDate))
				&& (date.isEqual(maxDate) || date.isBefore(maxDate)) && hour >= minHour);
		if (!isValid) {
			context.disableDefaultConstraintViolation();
			HibernateConstraintValidatorContext hibernateConstraintValidatorContext = context
					.unwrap(HibernateConstraintValidatorContext.class);
			hibernateConstraintValidatorContext.addMessageParameter("minDate",
					minDate.format(DateTimeFormatter.ofPattern(datePattern)));
			hibernateConstraintValidatorContext.addMessageParameter("maxDate",
					maxDate.format(DateTimeFormatter.ofPattern(datePattern)));
			hibernateConstraintValidatorContext.addMessageParameter("minHour", minHour);
			context.buildConstraintViolationWithTemplate("{validator.invalidStart}").addConstraintViolation();

		}
		return isValid;
	}

}
