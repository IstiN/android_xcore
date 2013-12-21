package by.istin.android.xcore.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import by.istin.android.xcore.utils.ResponderUtils;

/**
 * Created by IstiN on 5.12.13.
 */
public abstract class AbstractFagment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(getViewLayout(), container, false);
        onViewCreated(view);
        return view;
    }


    public void onViewCreated(View view) {

    }

    public abstract int getViewLayout();

    protected <T> T findFirstResponderFor(Class<T> clazz) {
        return ResponderUtils.findFirstResponderFor(this, clazz);
    }

}
