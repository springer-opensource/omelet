package omelet.exception;

/**
 * 
 * @author mlp8076
 *
 */
public class PageException extends GenericException {

	/**
	 * serial version use in case of serialization and de serialization
	 */
	private static final long serialVersionUID = 4189886755176914223L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @author mlp8076
	 * @param message
	 *            pass the message
	 * 
	 * @param pageName
	 *            pass the page in which exception is coming
	 */

	public PageException(String pageName, String message) {
		super("Exception in page: " + pageName + " is " + message);
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @author mlp8076
	 * @param message
	 *            pass the message
	 * 
	 * @param cause
	 *            pass the cause of exception
	 */
	public PageException(String message, Throwable cause) {
		super(message, cause);
	}
}
