package by.istin.android.xcore.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by IstiN on 5.12.13.
 */
public class ResponderUtils {

    public static <T> T findFirstResponderFor(Fragment fragment, Class<T> clazz) {
        FragmentActivity activity = fragment.getActivity();
        if (activity == null)
            return null;
        if (clazz.isInstance(activity)) {
            return clazz.cast(activity);
        }
        Fragment parentFragment = fragment.getParentFragment();
        while (parentFragment != null) {
            if (clazz.isInstance(parentFragment)) {
                return clazz.cast(parentFragment);
            }
            parentFragment = parentFragment.getParentFragment();
        }
        return null;
    }

}
