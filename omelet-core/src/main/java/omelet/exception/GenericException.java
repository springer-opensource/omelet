package omelet.exception;

/**
 * GenericException class is the parent class for all types of customized
 * exceptions
 * 
 * @author mlp8076
 *
 */
public class GenericException extends Exception {

	/**
	 * serial version use in case of serialization and de serialization
	 */
	private static final long serialVersionUID = 3743069803501918408L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 */
	public GenericException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of
	 * (cause==null ? null : cause.toString()) (which typically contains the class
	 * and detail message of cause).
	 * 
	 * @param cause
	 */
	public GenericException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message
	 * @param cause
	 */

	public GenericException(String message, Throwable cause) {
		super(message, cause);
	}

}
