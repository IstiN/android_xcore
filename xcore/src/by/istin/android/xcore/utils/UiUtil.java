package by.istin.android.xcore.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.view.Display;
import android.view.WindowManager;

import by.istin.android.xcore.ContextHolder;

/**
 * Class for converting px to the dp values, ellipsize text and else.
 * 
 * @author Uladzimir_Klyshevich
 *
 */
public class UiUtil {

	static int sDisplayWidth = -1;
	static int sDisplayHeight = -1;
	
	public static int getDisplayHeight() {
		if (sDisplayHeight == -1) {
			initDisplayDimensions();
		}
		return sDisplayHeight;
	}

	public static int getDisplayWidth() {
		if (sDisplayWidth == -1) {
			initDisplayDimensions();
		}
		return sDisplayWidth;
	}

	private static void initDisplayDimensions() {
		Context ctx = ContextHolder.getInstance().getContext();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			sDisplayWidth = size.x;
			sDisplayHeight = size.y;
		} else {
			sDisplayWidth = display.getWidth();
			sDisplayHeight = display.getHeight();
		}
	}
	
    /**
     * Default constructor.
     */
    private UiUtil() {
        // nothing here
    }

    /**
     * Convert px to dp.
     * @param context context
     * @param px value in px
     * @return dp value
     */
    public static Float getDp(final Context context, final Float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px * scale + 0.5f;
    }

    /**
     * Convert px to dp.
     * @param context context
     * @param px value in px
     * @return dp value
     */
    public static int getDp(final Context context, final int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Float.valueOf(px * scale + 0.5f).intValue();
    }

    /**
     * Gets fonts value for different resolutions.
     * @param context context
     * @param px value in px
     * @return sp value
     */
    public static int getFontSize(final Context context, final int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        int result = Float.valueOf(px * scale + 0.5f).intValue();
        if (result < 7) {
            result = result + 3;
        }
        return result;
    }

    /**
     * Convert dp value to the px value.
     * @param context context
     * @param dp value in dp
     * @return px value
     */
    public static Float getPx(final Context context, final Float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dp - 0.5f) / scale;
    }

    /**
     * Check device orientation.
     * @param context context
     * @return ture if prortrait else false
     */
    public static boolean isPortrait(final Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.orientation == Configuration.ORIENTATION_PORTRAIT;
    }
    
    /**
     * Constant contain small characters.
     */
    private final static String NON_THIN = "[^iIl1\\.,']";
    
    /**
     * Gets text width.
     * @param str string
     * @return value in px
     */
    private static int textWidth(String str) {
	    return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
	}

	/**
	 * Ellipsize text for lines.
	 * @param text text
	 * @param max max lines
	 * @return new text
	 */
	public static String ellipsize(String text, int max) {
		if (textWidth(text) <= max)
	        return text;

	    // Start by chopping off at the word before max
	    // This is an over-approximation due to thin-characters...
	    int end = text.lastIndexOf(' ', max - 3);

	    // Just one long word. Chop it off.
	    if (end == -1)
	        return text.substring(0, max-3) + "...";

	    // Step forward as long as textWidth allows.
	    int newEnd = end;
	    do {
	        end = newEnd;
	        newEnd = text.indexOf(' ', end + 1);

	        // No more spaces.
	        if (newEnd == -1)
	            newEnd = text.length();

	    } while (textWidth(text.substring(0, newEnd) + "...") < max);

	    return text.substring(0, end) + "...";
	}
}
