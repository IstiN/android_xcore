package by.istin.android.xcore.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.istin.android.xcore.utils.ResponderUtils;

/**
 * Created by IstiN on 5.12.13.
 */
public abstract class AbstractFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(getViewLayout(), container, false);
        onViewCreated(view);
        return view;
    }


    public void onViewCreated(View view) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IFragmentViewCreated fragmentViewCreated = findFirstResponderFor(IFragmentViewCreated.class);
        if (fragmentViewCreated != null) {
            fragmentViewCreated.onFragmentViewCreated(this);
        }
    }

    public abstract int getViewLayout();

    protected <T> T findFirstResponderFor(Class<T> clazz) {
        return ResponderUtils.findFirstResponderFor(this, clazz);
    }

}
