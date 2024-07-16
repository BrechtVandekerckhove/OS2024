package exceptions;

public class NoFreeSeatsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoFreeSeatsException(Integer id) {
		super(String.format("Could not find any free seats for game with id %s", id));
	}
}
