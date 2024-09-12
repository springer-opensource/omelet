package omelet.exception;

/**
 * Element exception class
 * 
 * @author mlp8076
 *
 */
public class ElementException extends GenericException {

	/**
	 * serial version use in case of serialization and de serialization
	 */
	private static final long serialVersionUID = 4419113553530968290L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *            pass the message
	 * @param element
	 *            pass the element type in which exception is coming
	 * @author mlp8076
	 */
	public ElementException(String element, String message) {
		super("Exception in element: " + element + " is " + message);
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *            pass the message
	 * @author mlp8076
	 */
	public ElementException(String message) {
		super("Exception during element interaction /t" + message);
	}

}
