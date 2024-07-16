package exceptions;

public class GamesNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GamesNotFoundException(Integer id) {
		super(String.format("Could not find any games of sport with id %s", id));
	}
}