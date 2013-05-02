package by.istin.android.xcore.test.bo;

import java.io.Serializable;

import android.content.ContentValues;

public class DialogsResponse implements Serializable {

	private static final long serialVersionUID = 577009158147303735L;

	private ContentValues[] dialogs;

	private ContentValues[] users;

	public ContentValues[] getDialogs() {
		return dialogs;
	}

	public ContentValues[] getUsers() {
		return users;
	}

	public void setDialogs(ContentValues[] dialogs) {
		this.dialogs = dialogs;
	}

	public void setUsers(ContentValues[] users) {
		this.users = users;
	}

}
