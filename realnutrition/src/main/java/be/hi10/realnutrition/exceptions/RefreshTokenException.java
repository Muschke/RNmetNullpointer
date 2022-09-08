package be.hi10.realnutrition.exceptions;

public class RefreshTokenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefreshTokenException(String message) {
		super(message);
	}

	public RefreshTokenException(String message, Throwable cause) {
		super(message, cause);
	}
}
