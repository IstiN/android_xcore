package by.istin.android.xcore.utils;

import android.content.ContentValues;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    private static final Object sLock = new Object();

    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("sha-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static long generateId(ContentValues contentValues, String ... keys) {
        Object[] values = new Object[keys.length];
        int i = 0;
        for (String k : keys) {
            values[i] = contentValues.get(k);
            i++;
        }
        return generateId(values);
    }

	public static long generateId(Object... value) {
        if (md == null) {
            return 0L;
        }
		//String value to be converted
        StringBuilder builder = new StringBuilder();
        for (Object s : value) {
            builder.append(String.valueOf(s));
        }
        byte[] hashValBytes;
        synchronized (sLock) {
            md.reset();
            //convert the string value to a byte array and pass it into the hash algorithm
            String s = builder.toString();
            md.update(StringUtil.getBytes(s));

            //retrieve a byte array containing the digest
            hashValBytes = md.digest();
        }

        long hashValLong = 0;

        //create a long value from the byte array
        for( int i = 0; i < 8; i++ ) {
            hashValLong |= ((long)(hashValBytes[i]) & 0x0FF)<<(8*i);
        }
        return hashValLong;
	}
	
}
