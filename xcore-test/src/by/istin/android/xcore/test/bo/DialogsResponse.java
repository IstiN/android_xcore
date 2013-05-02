package by.istin.android.xcore.test.bo;

import java.io.Serializable;

import android.content.ContentValues;

public class DialogsResponse implements Serializable {

	private static final long serialVersionUID = 577009158147303735L;
	
	public static class Response implements Serializable {

		private static final long serialVersionUID = 1284320803051083547L;

		private ContentValues[] dialogs;
		
		private ContentValues[] users;

		public ContentValues[] getDialogs() {
			return dialogs;
		}

		public ContentValues[] getUsers() {
			return users;
		}

	}
	
	private Response response;
	
	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}
