package by.istin.android.xcore.test.bo;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import android.content.ContentValues;

public class FriendsResponse implements Serializable {

	private static final long serialVersionUID = 577009158147303735L;
	
	@SerializedName(value="response")
	private ContentValues[] results;

	public ContentValues[] getResults() {
		return results;
	}
	
}
