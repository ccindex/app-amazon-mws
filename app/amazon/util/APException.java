package amazon.util;

/**
 * Title: APException.java
 *
 * @author Lc
 *
 * @date 2016年6月16日 下午5:14:14
 */
public class APException extends RuntimeException {
	
	public APException() {}

	public APException(String message) {
		super(message);
	}

	public APException(Throwable cause) {
		super(cause);
	}
	
	public APException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
