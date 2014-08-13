package by.istin.android.xcore.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;

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

    public static void clearCachedDisplayDimensions() {
        int sDisplayWidth = -1;
        int sDisplayHeight = -1;
    }

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

    public static boolean isWiFi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return  mWifi.isConnected();
    }

    @TargetApi(value = Build.VERSION_CODES.HONEYCOMB_MR2)
	private static void initDisplayDimensions() {
		Context ctx = ContextHolder.getInstance().getContext();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			sDisplayWidth = size.x;
			sDisplayHeight = size.y;
		} else {
			sDisplayWidth = display.getWidth();
			sDisplayHeight = display.getHeight();
		}
	}

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean setTranslucentBars(Activity activity) {
        return setTranslucent(activity, true, true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean setTranslucentNavigation(Activity activity) {
        return setTranslucent(activity, true, false);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean setTranslucentStatus(Activity activity) {
        return setTranslucent(activity, false, true);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean setTranslucent(Activity activity, boolean isNavigation, boolean isStatus) {
        if (!hasKitKat()) {
            return false;
        }
        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if(!hasMenuKey && !hasBackKey) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            int bits = 0;
            if (isNavigation) {
                bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                winParams.flags |=  bits;
            }
            if (isStatus) {
                bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                winParams.flags |=  bits;
            }
            win.setAttributes(winParams);
            return true;
        } else {
            return false;
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
	    return str.length() - str.replaceAll(NON_THIN, "").length() / 2;
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

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasL() {
        return VERSION.CODENAME.equalsIgnoreCase("L");
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Hide keyboard
     * @param view view, prefer set EditText
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        Activity activity = (Activity) view.getContext();
        if (activity == null) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT < 11) {
            Window window = activity.getWindow();
            if (window != null && window.getCurrentFocus() != null && window.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(),0);
            }
        } else {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null && currentFocus.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * Show keyboard
     * @param view view, prefer set EditText
     */
    public static void showKeyboard(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (android.os.Build.VERSION.SDK_INT < 11) {
            view.clearFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            view.requestFocus();
        } else {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setAlpha(View view, float alpha) {
        if (hasHoneycomb()) {
            view.setAlpha(alpha);
        } else {
            AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }
    }

    @TargetApi(Build.VERSION_CODES.L)
    public static void setViewName(View view, String viewName) {
        if (hasL()) {
            view.setViewName(viewName);
        }
    }

    public static void setViewName(View rootView, int id, String viewName) {
        setViewName(rootView.findViewById(id), viewName);
    }

    private static final int[] RES_IDS_ACTION_BAR_SIZE = { android.R.attr.actionBarSize };

    public static int getActionBarSize(Activity context) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        TypedArray att = curTheme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.L)
    public static void setElevation(View view, float value) {
        if (hasL()) {
            view.setClipToOutline(true);
            view.setElevation(value);
        }
    }
}
