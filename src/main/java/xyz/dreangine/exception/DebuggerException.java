package xyz.dreangine.exception;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import xyz.dreangine.exception.util.DebuggerExceptionUtil;

/**
 * This class has the purpose of easing the task of managing Exceptions
 * occurrences by allowing the programmer to catch this exception only
 * one time and yet being able to have specific error messages for each
 * method that has thrown a exception.<br>
 * By using a properties file to store the errors messages, it also allow
 * the error messages to be changed in real-time and without the need to
 * recompile the code.<br>
 * This class can be used either to wrap a caught exception or to throw
 * a new Exception (<b>DebuggerException</b>). The thrown <b>DebuggerException</b>
 * can be either logged/show right where it was generated or it can be
 * caught and logged by another class.
 * 
 * @author Omar V. Buede
 * @version 1.3a
 * @see DebuggerExceptionUtil
 */
public class DebuggerException extends Exception {
	private static final long serialVersionUID = -2431329576974759740L;
	private static final String EXCEPTIONS_FILE_NAME = "exceptions"; //$NON-NLS-1$
	private static final String MISSING_FILE_MESSAGE = "MISSING FILE: exceptions.properties!"; //$NON-NLS-1$
	private static final String DEBUG_KEY = "DEBUG"; //$NON-NLS-1$
	private static final String DEFAULT_MESSAGE_KEY = "defaultMessage"; //$NON-NLS-1$
	private static final String LINE_KEY = "line"; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$
	private static final String DOT = "."; //$NON-NLS-1$
	private static final String OPEN_PARENTHESIS = "("; //$NON-NLS-1$
	private static final String CLOSE_PARENTHESIS = ")"; //$NON-NLS-1$
	private static final String HYPHEN = "-"; //$NON-NLS-1$
	private ResourceBundle bundle;
	private StackTraceElement stack;
	private StringBuffer errorMessage;
	private boolean debug;
	
	/**
	 * Constructor for use when generating an exception.
	 */
	public DebuggerException() {
		loadBundle();
		this.errorMessage = new StringBuffer();
		this.debug = getDebugFromProperties();
		this.stack = getCorrectStack(this.getStackTrace());
	}
	
	/**
	 * Constructor for use when catching a thrown exception.
	 * 
	 * @param e thrown exception
	 */
	public DebuggerException(Exception e) {
		loadBundle();
		this.errorMessage = new StringBuffer();
		this.debug = getDebugFromProperties();
		this.stack = getCorrectStack(e.getStackTrace());
		this.errorMessage.append(SPACE);
		this.errorMessage.append(OPEN_PARENTHESIS);
		if(e.getMessage() != null) {
			this.errorMessage.append(e.getMessage());
			this.errorMessage.append(SPACE);
			this.errorMessage.append(HYPHEN);
			this.errorMessage.append(SPACE);
		}
		this.errorMessage.append(e.getClass().getName());
		this.errorMessage.append(CLOSE_PARENTHESIS);
	}
	
	/**
	 * This method can be used instead of the constructor that
	 * receives an exception as a parameter.<br>
	 * Just an aesthetic alternate.
	 * 
	 * @param e thrown exception
	 * @return an instance of <code>DebuggerException</code>
	 */
	public static DebuggerException instance(Exception e) {
		return new DebuggerException(e);
	}
	
	/**
	 * Method for checking if debug is enabled or disabled in this instance
	 * of the exception.
	 * 
	 * @return <code>true</code> if debug is enabled and <code>false</code>
	 * if disabled.
	 */
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * Method for enabling or disabling debug in this instance of the
	 * exception. The debug definition of the properties file or of
	 * other instances is not modified.
	 * 
	 * @param debug <code>true</code> to enable and <code>false</code>
	 * to disable.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Method for loading the properties file.
	 */
	private void loadBundle() {
		try {
			this.bundle = ResourceBundle.getBundle(EXCEPTIONS_FILE_NAME);
		} catch (MissingResourceException mre) {
			System.err.println(MISSING_FILE_MESSAGE);
		}
	}
	
	/**
	 * Method used to retrieve a value from the properties file.
	 * 
	 * @param key represents the key from the key-value pair
	 * in the properties file
	 * @return the value from the key-value pair
	 */
	private String getMessage(String key) {
		try {
			return this.bundle.getString(key);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Method that searches, in the current stack, for the first element that has
	 * a key in the properties file. If none is found, then the top element is
	 * returned.
	 * 
	 * @param stacks the elements that are stacked
	 * @return the first element that has a key in the properties file or the
	 * top element in the stack
	 */
	private StackTraceElement getCorrectStack(StackTraceElement[] stacks) {
		StackTraceElement currentStack;
		String currentKey, currentMessage;
		
		for (int i = 0; i < stacks.length; i++) {
			currentStack = stacks[i];
			currentKey = currentStack.getClassName() + DOT + currentStack.getMethodName();
			
			currentMessage = getMessage(currentKey);
			if(currentMessage != null) {
				this.errorMessage.append(currentMessage);
				return currentStack;
			}
		}
		this.errorMessage.append(getMessage(DEFAULT_MESSAGE_KEY));
		
		return stacks[0];
	}
	
	/**
	 * Method that retrieves the value of the DEBUG key from the properties
	 * file.
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	private boolean getDebugFromProperties() {
		return Boolean.parseBoolean(getMessage(DEBUG_KEY));
	}
	
	/**
	 * Method that returns the error message for the current exception
	 * 
	 * @return the error message
	 */
	@Override
	public String toString() {
		StringBuffer message = new StringBuffer();
		
		if(this.debug) {
			message.append(OPEN_PARENTHESIS); 
			message.append(this.stack.getClassName());
			message.append(DOT);
			message.append(this.stack.getMethodName());
			message.append(SPACE); 
			message.append(HYPHEN); 
			message.append(SPACE); 
			message.append(getMessage(LINE_KEY));
			message.append(SPACE);
			message.append(this.stack.getLineNumber());
			message.append(CLOSE_PARENTHESIS);
			message.append(SPACE); 
		}
		message.append(this.errorMessage);
		
		return message.toString();
	}
}