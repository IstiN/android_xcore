package by.istin.android.xcore.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;


public class Intents {

    /**
     * Open browser.
     *
     * @param context the context
     * @param url     the url
     */
    public static void openBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Please install browser", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Share text.
     *
     * @param context the context
     * @param text    the text
     */
    public static void shareText(Context context, String text) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        context.startActivity(intent);
    }

    /**
     * Share text.
     *
     * @param context the context
     * @param text    the text
     */
    public static void shareText(Context context, String subject, String text) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        context.startActivity(intent);
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void playVideo(final Context ctx, String playUrl) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(playUrl), "video/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "player not found", Toast.LENGTH_SHORT).show();
        }
    }
}
