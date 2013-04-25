package by.istin.android.xcore.source.impl.http.exception;

import java.io.IOException;

/**
 * This exception will throw if gets not valid status.
 * @author Uladzimir_Klyshevich
 */
public class IOStatusException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5390007874342523153L;

	private int statusCode;
	
	private String entityValue;
	
	
	public int getStatusCode() {
		return statusCode;
	}

	
	public String getEntityValue() {
		return entityValue;
	}



	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	/**
	 * Default constructor.
	 */
	public IOStatusException() {
		super();
	}


	/**
	 * Constructor with detail message.
	 * @param detailMessage detail message
	 */
	public IOStatusException(String detailMessage, int statusCode) {
		super(detailMessage);
		this.statusCode = statusCode;
	}



	public IOStatusException(String reasonPhrase, int statusCode,
			String entityValue) {
		this(reasonPhrase, statusCode);
		this.entityValue = entityValue;
	}

}
