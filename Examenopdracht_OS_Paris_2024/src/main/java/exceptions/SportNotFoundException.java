package exceptions;

public class SportNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SportNotFoundException(Integer id) {
		super(String.format("Could not find sport with id %s", id));
	}

}
