package by.istin.android.xcore.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

	public static long generateId(String value) {
		//String value to be converted
		try {
			MessageDigest md = MessageDigest.getInstance("sha-1");
		
			//convert the string value to a byte array and pass it into the hash algorithm
			md.update(value.getBytes());
	
			//retrieve a byte array containing the digest
			byte[] hashValBytes = md.digest();
	
			long hashValLong = 0;
	
			//create a long value from the byte array
			for( int i = 0; i < 8; i++ ) {
			    hashValLong |= ((long)(hashValBytes[i]) & 0x0FF)<<(8*i);
			}
			return hashValLong;
		} catch (NoSuchAlgorithmException e) {
			return 0l;
		}

	}
	
}
