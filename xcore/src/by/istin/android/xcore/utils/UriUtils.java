package by.istin.android.xcore.utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import android.net.Uri;
import android.webkit.MimeTypeMap;

public class UriUtils {

	public static Set<String> getQueryParameters(Uri uri) {
		if (uri.isOpaque()) {
			return Collections.emptySet();
		}
		String query = uri.getEncodedQuery();
		if (query == null) {
			return Collections.emptySet();
		}
		Set<String> names = new LinkedHashSet<String>();
		int start = 0;
		do {
			int next = query.indexOf('&', start);
			int end = (next == -1) ? query.length() : next;

			int separator = query.indexOf('=', start);
			if (separator > end || separator == -1) {
				separator = end;
			}

			String name = query.substring(start, separator);
			names.add(Uri.decode(name));

			// Move start to end of name.
			start = end + 1;
		} while (start < query.length());
		return Collections.unmodifiableSet(names);
	}


	public static String negotiateMimeTypeFromUri(Uri uri) {
		String ext = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
	}
}
