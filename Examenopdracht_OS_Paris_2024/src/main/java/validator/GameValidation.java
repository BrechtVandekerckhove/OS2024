
package validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import domain.Game;
import repository.GameRepository;

public class GameValidation implements Validator {

	private final int RANGE = 1000;
	private final int LENGTH = 5;
	@Autowired
	private GameRepository gameRepository;

	@Override
	public boolean supports(Class<?> klass) {
		return Game.class.isAssignableFrom(klass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Game game = (Game) target;

		int olympicNumber1 = game.getOlympicNumber1();
		int olympicNumber2 = game.getOlympicNumber2();

		List<Integer> listOlympicNumber1s = gameRepository.getAllOlympicNumber1s();

		if (listOlympicNumber1s.contains(olympicNumber1)) {
			errors.rejectValue("olympicNumber1", "validator.invalidOlympicNumber1Known");
		}

		if (!(olympicNumber1 >= Math.pow(10, LENGTH - 1) && olympicNumber1 < Math.pow(10, LENGTH)
				&& olympicNumber1 / (int) Math.pow(10, LENGTH- 1) != olympicNumber1 % 10)) {
			errors.rejectValue("olympicNumber1", "validator.invalidOlympicNumber1", new Object[] { LENGTH }, "");
		}
		if (olympicNumber2 < olympicNumber1 - RANGE  || olympicNumber2 > olympicNumber1 + RANGE) {
			errors.rejectValue("olympicNumber2", "validator.invalidOlympicNumber2", new Object[] { RANGE }, "");

		}

		if (game.getDisciplines().size() == 2 && game.getDisciplines().get(0) == game.getDisciplines().get(1)) {
			errors.rejectValue("disciplines", "validator.invalidDisciplines");
		}

	}
}
