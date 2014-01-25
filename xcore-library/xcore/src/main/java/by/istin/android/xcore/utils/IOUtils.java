package by.istin.android.xcore.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class IOUtils {

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				//can be ignored
			}
		}
	}

	public static void close(ObjectOutput closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
                //can be ignored
            }
        }
	}

	public static void close(ObjectInput closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
                //can be ignored
            }
        }
	}

}
