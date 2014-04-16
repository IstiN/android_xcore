/**
 * 
 */
package by.istin.android.xcore.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import by.istin.android.xcore.utils.Log;

/**
 * Abstract base class for all model classes. 
 * Contains common accessors and mutators for JSON values and basic implementation of Parcelable interface.
 * 
 * @author Uladzimir_Klyshevich
 *
 */
public class JSONModel implements Parcelable {
	
	/**
	 * Interface for create {@link JSONModel}
	 * @author Uladzimir_Klyshevich
	 *
	 * @param <T> Class which need create
	 */
	public interface ICreator<T extends JSONModel> {
		T create(JSONObject jsonObject);
	}
	
	/** The constant logging tag. */
	public static final String TAG = JSONModel.class.getName();

	/** The underlying JSON object. */
	private JSONObject jo;

	/**
	 * Instantiates a new base model.
	 */
	public JSONModel() {
		jo = new JSONObject();
		init();
	}

	/**
	 * Instantiates a new base model with underlying JSON object as String.
	 * 
	 * @param json
	 *            the json
	 * @throws org.json.JSONException
	 *             the jSON exception
	 */
	public JSONModel(final String json) throws JSONException {
		jo = new JSONObject(json);
		init();
	}

	/**
	 * Instantiates a new base model with underlying JSON object.
	 * 
	 * @param json
	 *            the json object
	 */
	public JSONModel(final JSONObject json) {
		if (json == null) {
			throw new IllegalArgumentException("JSONObject argument is null");
		}
		jo = json;
		init();
	}

	/**
	 * Sets the value for key.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	protected final void set(final String key, final Object value) {
		try {
			synchronized (jo) {
				if (value == null) {
					jo.remove(key);
				} else {
					jo.put(key, value);
				}				
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
	}
	
	protected final void setModel(final String key, final JSONModel model) {
		synchronized (jo) {
			if (model == null) {
				jo.remove(key);
			} else {
				try {
					jo.put(key, model.getJSONObject());
				} catch (JSONException e) {
					Log.e(TAG, e);
				}
			}
		}
	}

	/**
	 * Gets the value for key.
	 * 
	 * @param key
	 *            the key
	 * @return the object
	 */
	protected final Object get(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.get(key);
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the String value.
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	protected final String getString(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getString(key);
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the inner string from nested JSON object.
	 * 
	 * @param jkey
	 *            the nested JSON object jkey
	 * @param key
	 *            the String key
	 * @return the inner string
	 */
	protected final String getInnerString(final String jkey, final String key) {
		try {
			JSONObject ijo = getJSONObject(jkey);
			if (ijo == null) {
				return null;
			}
			return ijo.getString(key);
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;

	}

	/**
	 * Gets the Integer value.
	 * 
	 * @param key
	 *            the key
	 * @return the int
	 */
	protected final Integer getInt(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getInt(key);
			} 
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the Double value.
	 * 
	 * @param key
	 *            the key
	 * @return the double
	 */
	protected final Double getDouble(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getDouble(key);
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the Boolean value.
	 * 
	 * @param key
	 *            the key
	 * @return the boolean
	 */
	protected final Boolean getBoolean(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getBoolean(key);
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the Long value.
	 * 
	 * @param key
	 *            the key
	 * @return the long
	 */
	protected final Long getLong(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getLong(key);
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the Color int value.
	 * 
	 * @param key
	 *            the key
	 * @return the Color int value
	 */
	protected Integer getColor(String key) {
		String colorStr = getString(key);
		if (colorStr == null) {
			return null;
		}
		return Color.parseColor("#"+ colorStr);
	}
	
	/**
	 * Gets the JSON object value.
	 * 
	 * @param key
	 *            the key
	 * @return the jSON object
	 */
	protected final JSONObject getJSONObject(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getJSONObject(key);
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the JSONArray value.
	 * 
	 * @param key
	 *            the key
	 * @return the JSONArray
	 */
	protected final JSONArray getJSONArray(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getJSONArray(key);
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the JSONArray value size.
	 * 
	 * @param key
	 *            the key
	 * @return the jSON array size
	 */
	protected final Integer getJSONArraySize(final String key) {
		try {
			if (!jo.isNull(key)) {
				return jo.getJSONArray(key).length();
			}
		} catch (JSONException e) {
			Log.e(TAG, e);
		}
		return null;
	}

	/**
	 * Gets the underlying JSON object.
	 * 
	 * @return the jSON object
	 */
	public final JSONObject getJSONObject() {
		return jo;
	}

	///////////////// Parcelable implementation /////////////////

	/**
	 * Instantiates a new base model from Parcel sorce.
	 * 
	 * @param source
	 *            the source
	 */
	public JSONModel(final Parcel source) {
		readFromParcel(source);
		init();
	}

	protected void init() {
		
	}

	/**
	 * Read from parcel.
	 * 
	 * @param in
	 *            the in
	 */
	protected void readFromParcel(final Parcel in) {
		Serializable serializable = in.readSerializable();
		try {
			jo = new JSONObject((String) serializable);
		} catch (Exception e) {
			Log.e(TAG, "Cannot serialize to JSONObject");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public final int describeContents() {
		return 0;
	}

	/*
	 * 
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel parcel, final int i) {
		synchronized (jo) {
			parcel.writeSerializable(jo.toString());			
		}
	}

	public void setJO(JSONObject object) {
		this.jo = object;
	}

    @Override
    public String toString() {
        if (jo != null) {
            return jo.toString();
        }
        return super.toString();
    }
}