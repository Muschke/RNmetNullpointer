package be.hi10.realnutrition.exceptions;

public class ApiException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String itemId;

	public ApiException(String message) {
		super(message);
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiException(String message, Throwable cause, String itemId) {
		super(message, cause);
		setItemId(itemId);
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

}
