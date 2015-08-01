package by.istin.android.xcore.utils;

import android.content.ContentValues;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.google.common.internal.net.PercentEscaper;

import org.apache.commons.codec.internal.DecoderException;
import org.apache.commons.codec.internal.net.URLCodec;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.ContextHolder;

/**
 * The Class StringUtil. Provides set of common low-level string functions.
 */
public final class StringUtil {

    public static final String DEFAULT_ENCODING = "UTF-8";

    private static final URLCodec URL_CODEC = new URLCodec(DEFAULT_ENCODING);

    private static final PercentEscaper PERCENT_ESCAPER = new PercentEscaper("-_.*", false);

    /**
     * Empty string array
     */
    public static final String[] STRING_ARRAY = new String[]{};

    /**
     * Constant value for "_".
     */
    public static final char _ = '_';

    /**
     * Constant value for string.
     */
    private static final String STRING = "string";

    /**
     * The Constant SPACE.
     */
    private static final String SPACE = " ";

    /**
     * The Constant EMPTY.
     */
    public static final String EMPTY = "";

    /**
     * Html entites as escape values.
     */
    private static final HashMap<String, String> htmlEntities;

    private static final ConcurrentHashMap<Character, String> russianAlternative;

    static {
        htmlEntities = new HashMap<String, String>();
        htmlEntities.put("&lt;", "<");
        htmlEntities.put("&gt;", ">");
        htmlEntities.put("&amp;", "&");
        htmlEntities.put("&quot;", "\"");
        htmlEntities.put("&agrave;", "à");
        htmlEntities.put("&Agrave;", "À");
        htmlEntities.put("&acirc;", "â");
        htmlEntities.put("&auml;", "ä");
        htmlEntities.put("&Auml;", "Ä");
        htmlEntities.put("&Acirc;", "Â");
        htmlEntities.put("&aring;", "å");
        htmlEntities.put("&Aring;", "Å");
        htmlEntities.put("&aelig;", "æ");
        htmlEntities.put("&AElig;", "Æ");
        htmlEntities.put("&ccedil;", "ç");
        htmlEntities.put("&Ccedil;", "Ç");
        htmlEntities.put("&eacute;", "é");
        htmlEntities.put("&Eacute;", "É");
        htmlEntities.put("&egrave;", "è");
        htmlEntities.put("&Egrave;", "È");
        htmlEntities.put("&ecirc;", "ê");
        htmlEntities.put("&Ecirc;", "Ê");
        htmlEntities.put("&euml;", "ë");
        htmlEntities.put("&Euml;", "Ë");
        htmlEntities.put("&iuml;", "ï");
        htmlEntities.put("&Iuml;", "Ï");
        htmlEntities.put("&ocirc;", "ô");
        htmlEntities.put("&Ocirc;", "Ô");
        htmlEntities.put("&ouml;", "ö");
        htmlEntities.put("&Ouml;", "Ö");
        htmlEntities.put("&oslash;", "ø");
        htmlEntities.put("&Oslash;", "�?");
        htmlEntities.put("&szlig;", "ß");
        htmlEntities.put("&ugrave;", "ù");
        htmlEntities.put("&Ugrave;", "Ù");
        htmlEntities.put("&ucirc;", "û");
        htmlEntities.put("&Ucirc;", "Û");
        htmlEntities.put("&uuml;", "ü");
        htmlEntities.put("&Uuml;", "Ü");
        htmlEntities.put("&nbsp;", " ");
        htmlEntities.put("&copy;", "\u00a9");
        htmlEntities.put("&reg;", "\u00ae");
        htmlEntities.put("&euro;", "\u20a0");

        russianAlternative = new ConcurrentHashMap<>();
        russianAlternative.put('а', "a");
        russianAlternative.put('б', "b");
        russianAlternative.put('в', "v");
        russianAlternative.put('г', "g");
        russianAlternative.put('д', "d");
        russianAlternative.put('е', "e");
        russianAlternative.put('ё', "e");
        russianAlternative.put('ж', "zh");
        russianAlternative.put('з', "z");
        russianAlternative.put('и', "i");
        russianAlternative.put('й', "y");
        russianAlternative.put('к', "k");
        russianAlternative.put('л', "l");
        russianAlternative.put('м', "m");
        russianAlternative.put('н', "n");
        russianAlternative.put('о', "o");
        russianAlternative.put('п', "p");
        russianAlternative.put('р', "r");
        russianAlternative.put('с', "s");
        russianAlternative.put('т', "t");
        russianAlternative.put('у', "u");
        russianAlternative.put('ф', "f");
        russianAlternative.put('х', "h");
        russianAlternative.put('ц', "ts");
        russianAlternative.put('ч', "ch");
        russianAlternative.put('ш', "sh");
        russianAlternative.put('щ', "sht");
        russianAlternative.put('ы', "y");
        russianAlternative.put('ь', "y");
        russianAlternative.put('ъ', "a");
        russianAlternative.put('э', "e");
        russianAlternative.put('ю', "yu");
        russianAlternative.put('я', "ya");
        russianAlternative.put('А', "A");
        russianAlternative.put('Б', "B");
        russianAlternative.put('В', "V");
        russianAlternative.put('Г', "G");
        russianAlternative.put('Д', "D");
        russianAlternative.put('Е', "E");
        russianAlternative.put('Ё', "E");
        russianAlternative.put('Ж', "Zh");
        russianAlternative.put('З', "Z");
        russianAlternative.put('И', "I");
        russianAlternative.put('Й', "Y");
        russianAlternative.put('К', "K");
        russianAlternative.put('Л', "L");
        russianAlternative.put('М', "M");
        russianAlternative.put('Н', "N");
        russianAlternative.put('О', "O");
        russianAlternative.put('П', "P");
        russianAlternative.put('Р', "R");
        russianAlternative.put('С', "S");
        russianAlternative.put('Т', "T");
        russianAlternative.put('У', "U");
        russianAlternative.put('Ф', "F");
        russianAlternative.put('Х', "H");
        russianAlternative.put('Ц', "Ts");
        russianAlternative.put('Ч', "Ch");
        russianAlternative.put('Ш', "Sh");
        russianAlternative.put('Щ', "Sht");
        russianAlternative.put('Ы', "Y");
        russianAlternative.put('Ь', "Y");
        russianAlternative.put('Ъ', "A");
        russianAlternative.put('Э', "E");
        russianAlternative.put('Ю', "Yu");
        russianAlternative.put('Я', "Ya");
    }

    /**
     * bt Hidden constructor.
     */
    private StringUtil() {

    }

    /**
     * Unescape html.
     *
     * @param source source
     * @param start  from start
     * @return unscape html value
     */
    public static String unescapeHTML(String source, int start) {
        int i, j;

        i = source.indexOf("&", start);
        if (i > -1) {
            j = source.indexOf(";", i);
            if (j > i) {
                String entityToLookFor = source.substring(i, j + 1);
                String value = htmlEntities.get(entityToLookFor);
                if (value != null) {
                    source = source.substring(0, i) + value + source.substring(j + 1);
                    return unescapeHTML(source, i + 1); // recursive call
                }
            }
        }
        return source;
    }

    /**
     * Translit.
     *
     * @param source source
     * @return translited value
     */
    public static String translit(String source) {
        if (isEmpty(source)) {
            return source;
        }
        int length = source.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = source.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                stringBuilder.append(c);
            } else {
                String s = russianAlternative.get(c);
                if (s != null) {
                    stringBuilder.append(s);
                } else {
                    stringBuilder.append(c);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Gets the not null.
     *
     * @param value the value
     * @return the not null
     */
    public static String getNotNull(final String value) {
        if (value == null) {
            return EMPTY;
        }
        return value;
    }

    /**
     * Checks string length minimum.
     *
     * @param x   the string value for checking
     * @param min the minimum length for string value
     * @return true, if string not null and its length not less than specified
     */
    public static boolean checkMinLength(final String x, final int min) {
        return !(x == null || x.length() < min);
    }

    /**
     * Check string for containing space.
     *
     * @param x the string value for checking
     * @return true, if string not null and not contain space
     */
    public static boolean checkContainSpace(final String x) {
        return !(x != null && x.contains(SPACE));
    }

    public static String format(String valueToFormat, Object... args) {
        return String.format(Locale.ENGLISH, valueToFormat, args);
    }

    public static String getKeyByName(String name) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch) && builder.length() != 0) {
                builder.append(_);
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }

    /**
     * Check empty string.
     *
     * @param s string
     * @return true if empty, else false
     */
    public static boolean isEmpty(Object s) {
        return (s == null || "".equals(s));
    }

    /**
     * Check equals two strings.
     *
     * @param s1 string1
     * @param s2 string2
     * @return true if equals else false
     */
    public static boolean isEquals(String s1, Object s2) {
        return s1 == null ? (s2 == null) : s1.equals(s2);
    }

    /**
     * Comare two string
     *
     * @param s1 string1
     * @param s2 string2
     * @return result
     */
    public static int compare(String s1, String s2) {
        return s1 == null ? (s2 == null ? 0 : -1) : s1.compareTo(s2);
    }

    /**
     * Gets first string or null from list.
     *
     * @param list list
     * @return result
     */
    public static String firstOrNull(List<String> list) {
        return list == null || list.size() == 0 ? null : list.get(0);
    }

    /**
     * Gets string after separator.
     *
     * @param source source
     * @param sep    separator
     * @return result
     */
    public static String stringAfter(String source, String sep) {

        if (source == null)
            return null;
        int p = source.indexOf(sep);
        return p == -1 ? source : source.substring(p + sep.length());
    }

    /**
     * Gets string value from resource by key.
     *
     * @param key key of resource
     * @return String value
     */
    public static String getStringResource(String key, Context context) {
        if (context == null) {
            return null;
        }
        final int linkCallIdentificator = context.getResources().getIdentifier(
                key, STRING, context.getPackageName());
        if (linkCallIdentificator == 0) {
            return "["+key+"]";
        }
        return context.getString(linkCallIdentificator);
    }

    /**
     * Gets string value from resource by key. Required {@link ContextHolder}.
     *
     * @param key key of resource
     * @return String value
     */
    public static String getStringResource(String key) {
        return getStringResource(key, ContextHolder.get());
    }

    public static String encode(String value, String defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        return PERCENT_ESCAPER.escape(value);
    }

    public static String encode(String value) {
        return encode(value, null);
    }

    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URL_CODEC.decode(value);
        } catch (IllegalArgumentException e) {
            return value;
        } catch (DecoderException e) {
            return value;
        }

    }

    public static Spanned fromHtml(String value) {
        if (StringUtil.isEmpty(value)) {
            return Html.fromHtml(EMPTY);
        }
        return Html.fromHtml(value);
    }

    public static String join(CharSequence delimiter, boolean skipNull, Object... values) {
        if (skipNull) {
            List<Object> objects = new ArrayList<Object>();
            for (Object o : values) {
                if (!StringUtil.isEmpty(o)) {
                    objects.add(o);
                }
            }
            return joinAll(delimiter, objects.toArray(new Object[objects.size()]));
        } else {
            return joinAll(delimiter, values);
        }
    }

    public static String joinAll(CharSequence delimiter, Object... values) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : values) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            if (token instanceof Object[]) {
                Object[] array = (Object[]) token;
                sb.append("[");
                sb.append(joinAll(",", array));
                sb.append("]");
            } else {
                sb.append(token);
            }
        }
        return sb.toString();
    }

    public static String joinAll(CharSequence delimiter, List<ContentValues> values, String column) {
        List<String> arrayList = new ArrayList<String>();
        for (ContentValues contentValues : values) {
            arrayList.add(contentValues.getAsString(column));
        }
        return TextUtils.join(delimiter, arrayList);
    }

    public static String makeJoinedPlaceholders(String value, String delimiter, int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append(value);
            for (int i = 1; i < len; i++) {
                sb.append(delimiter);
                sb.append(value);
            }
            return sb.toString();
        }
    }

    public static String[] toStringArray(Collection collection) {
        if (collection == null) {
            return null;
        }
        String[] strings = new String[collection.size()];
        int i = 0;
        for (Object o : collection) {
            strings[i] = String.valueOf(o);
            i++;
        }
        return strings;
    }

    public static byte[] getBytes(String s) {
        try {
            return s.getBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String newString(byte[] byteArray) {
        try {
            return new String(byteArray, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
