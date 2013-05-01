package by.istin.android.xcore.test.bo;

import java.io.Serializable;

import android.content.ContentValues;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.test.vk.Dialog;

import com.google.gson.annotations.SerializedName;

public class DialogsResponse implements Serializable {

	private static final long serialVersionUID = 577009158147303735L;
	
	@SerializedName(value="response:dialogs")
	@dbEntities(clazz=Dialog.class)
	public static final String DIALOGS = "dialogs";

	@SerializedName(value="users")
	private ContentValues[] users;
	
	public ContentValues[] getUsers() {
		return users;
	}
	
}
