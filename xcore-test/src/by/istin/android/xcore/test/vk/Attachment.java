package by.istin.android.xcore.test.vk;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IBeforeUpdate;
import by.istin.android.xcore.utils.HashUtils;

import com.google.gson.annotations.SerializedName;

public class Attachment implements BaseColumns, IBeforeUpdate {

	@dbLong
	public static final String ID = _ID;
	
	@dbString
	public static final String TYPE = "type";
	
	@dbLong
	public static final String MESSAGE_ID = "message_id";
	
	@dbLong
	public static final String DIALOG_ID = "dialog_id";

	/* ===== DOC ===== */
	@dbLong
	@SerializedName(value="doc:did")
	public static final String DOC_ID = "did";
	
	@dbLong
	@SerializedName(value="doc:owner_id")
	public static final String DOC_OWNER_ID = "doc_owner_id";
	
	@dbLong
	@SerializedName(value="doc:size")
	public static final String DOC_SIZE = "doc_size";
	
	@dbString
	@SerializedName(value="doc:title")
	public static final String DOC_TITLE = "doc_title";
	
	@dbString
	@SerializedName(value="doc:ext")
	public static final String DOC_EXT = "doc_ext";
	
	@dbString
	@SerializedName(value="doc:url")
	public static final String DOC_URL = "doc_url";
	
	@dbString
	@SerializedName(value="doc:access_key")
	public static final String DOC_ACCESS_KEY = "doc_access_key";
	
	/* ===== AUDIO ===== */
	@dbLong
	@SerializedName(value="audio:aid")
	public static final String AUDIO_ID = "aid";
	
	@dbLong
	@SerializedName(value="audio:owner_id")
	public static final String AUDIO_OWNER_ID = "audio_owner_id";
	
	@dbInteger
	@SerializedName(value="audio:duration")
	public static final String AUDIO_DURATION = "audio_duration";
	
	@dbString
	@SerializedName(value="audio:artist")
	public static final String AUDIO_ARTIST = "audio_artist";
	
	@dbString
	@SerializedName(value="audio:title")
	public static final String AUDIO_TITLE = "audio_title";
	
	@dbString
	@SerializedName(value="audio:url")
	public static final String AUDIO_URL = "audio_url";
	
	@dbString
	@SerializedName(value="audio:performer")
	public static final String AUDIO_PERFORMER = "audio_performer";
	
	@dbString
	@SerializedName(value="audio:album")
	public static final String AUDIO_ALBUM = "audio_album";
	
	/* ===== PHOTO ===== */
	@dbLong
	@SerializedName(value="photo:pid")
	public static final String PHOTO_ID = "pid";
	
	@dbLong
	@SerializedName(value="photo:aid")
	public static final String PHOTO_AID = "photo_aid";
	
	@dbLong
	@SerializedName(value="photo:owner_id")
	public static final String PHOTO_OWNER_ID = "photo_owner_id";
	
	@dbLong
	@SerializedName(value="photo:created")
	public static final String PHOTO_CREATED = "photo_created";
	
	@dbInteger
	@SerializedName(value="photo:width")
	public static final String PHOTO_WIDTH = "photo_width";
	
	@dbInteger
	@SerializedName(value="photo:height")
	public static final String PHOTO_HEIGHT = "photo_height";
	
	@dbDouble
	@SerializedName(value="photo:lat")
	public static final String PHOTO_LAT = "photo_lat";
	
	@dbDouble
	@SerializedName(value="photo:long")
	public static final String PHOTO_LONG = "photo_long";
	
	@dbString
	@SerializedName(value="photo:src")
	public static final String PHOTO_SRC = "photo_src";
	
	@dbString
	@SerializedName(value="photo:src_big")
	public static final String PHOTO_SRC_BIG = "photo_src_big";
	
	@dbString
	@SerializedName(value="photo:src_small")
	public static final String PHOTO_SRC_SMALL = "photo_src_small";
	
	@dbString
	@SerializedName(value="photo:src_xbig")
	public static final String PHOTO_SRC_XBIG = "photo_src_xbig";
	
	@dbString
	@SerializedName(value="photo:src_xxbig")
	public static final String PHOTO_SRC_XXBIG = "photo_src_xxbig";
	
	@dbString
	@SerializedName(value="photo:src_xxxbig")
	public static final String PHOTO_SRC_XXXBIG = "photo_src_xxxbig";
	
	@dbString
	@SerializedName(value="photo:text")
	public static final String PHOTO_TEXT = "photo_text";
	
	@dbString
	@SerializedName(value="photo:access_key")
	public static final String PHOTO_ACCESS_KEY = "photo_access_key";
	
	/* ===== VIDEO ===== */
	@dbLong
	@SerializedName(value="video:vid")
	public static final String VIDEO_ID = "vid";
	
	@dbLong
	@SerializedName(value="video:owner_id")
	public static final String VIDEO_OWNER_ID = "video_owner_id";
	
	@dbLong
	@SerializedName(value="video:date")
	public static final String VIDEO_DATE = "video_date";
	
	@dbLong
	@SerializedName(value="video:views")
	public static final String VIDEO_VIEWS = "video_views";
	
	@dbLong
	@SerializedName(value="video:duration")
	public static final String VIDEO_DURATION = "video_duration";
	
	@dbString
	@SerializedName(value="video:title")
	public static final String VIDEO_TITLE = "video_title";
	
	@dbString
	@SerializedName(value="video:description")
	public static final String VIDEO_DESCRIPTION = "video_description";
	
	@dbString
	@SerializedName(value="video:image")
	public static final String VIDEO_IMAGE = "video_image";
	
	@dbString
	@SerializedName(value="video:image_big")
	public static final String VIDEO_IMAGE_BIG = "video_image_big";
	
	@dbString
	@SerializedName(value="video:image_small")
	public static final String VIDEO_IMAGE_SMALL = "video_image_small";
	
	@dbString
	@SerializedName(value="video:image_xbig")
	public static final String VIDEO_IMAGE_XBIG = "video_image_xbig";
	
	@dbString
	@SerializedName(value="video:access_key")
	public static final String VIDEO_ACCESS_KEY = "video_access_key";
	
	/* ===== GEO ===== */
	@dbString
	@SerializedName(value="geo:type")
	public static final String GEO_TYPE = "geo_type";
	
	@dbString
	@SerializedName(value="geo:coordinates")
	public static final String GEO_COORDINATES = "geo_coordinates";
	
	@dbString
	@SerializedName(value="geo:place:title")
	public static final String GEO_PLACE_TITLE = "geo_place_title";
	
	@dbString
	@SerializedName(value="geo:place:country")
	public static final String GEO_PLACE_COUNTRY = "geo_place_country";
	
	@dbString
	@SerializedName(value="geo:place:city")
	public static final String GEO_PLACE_CITY = "geo_place_city";
	
	
	@Override
	public void onBeforeUpdate(ContentValues contentValues) {
		String hashValue = contentValues.getAsString(TYPE) 
				+ contentValues.getAsLong(DOC_ID) 
				+ contentValues.getAsLong(AUDIO_ID) 
				+ contentValues.getAsLong(VIDEO_ID)  
				+ contentValues.getAsLong(PHOTO_ID) 
				+ contentValues.getAsLong(MESSAGE_ID) 
				+ contentValues.getAsLong(DIALOG_ID) 
				+ contentValues.getAsString(GEO_COORDINATES);
		contentValues.put(_ID, HashUtils.generateId(hashValue));
	}
	
}