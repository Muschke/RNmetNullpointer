package be.hi10.realnutrition.exceptions;

public class HashException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HashException(String message) {
		super(message);
	}

	public HashException(String message, Throwable cause) {
		super(message, cause);
	}
}
