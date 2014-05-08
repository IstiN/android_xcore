package by.istin.android.xcore.test.bo;

import java.io.Serializable;

import android.content.ContentValues;

import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.test.vk.Dialog;
import by.istin.android.xcore.test.vk.User;

public class DialogsResponse implements Serializable {

	private static final long serialVersionUID = 577009158147303735L;

    @dbEntity(clazz = Dialog.class)
	private ContentValues[] dialogs;

    @dbEntity(clazz = User.class)
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
