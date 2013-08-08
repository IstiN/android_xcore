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

	private int mStatusCode;
	
	private String mEntityValue;
	
	
	public int getStatusCode() {
		return mStatusCode;
	}

	
	public String getEntityValue() {
		return mEntityValue;
	}



	public void setStatusCode(int statusCode) {
		this.mStatusCode = statusCode;
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
		this.mStatusCode = statusCode;
	}



	public IOStatusException(String reasonPhrase, int statusCode,
			String entityValue) {
		this(reasonPhrase, statusCode);
		this.mEntityValue = entityValue;
	}

}
