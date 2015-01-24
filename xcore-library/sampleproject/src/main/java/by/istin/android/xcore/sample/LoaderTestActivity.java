package by.istin.android.xcore.sample;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import by.istin.android.xcore.fragment.AbstractFragment;

public class LoaderTestActivity extends AbstractActivity {


    @Override
    protected Fragment createFragment() {
        return new PlaceHolderFragment();
    }

    public static class PlaceHolderFragment extends AbstractFragment {

        @Override
        public int getViewLayout() {
            return R.layout.fragment_loader_test;
        }

        @Override
        public void onViewCreated(View view) {
            super.onViewCreated(view);
        }
    }

    public void onNotifyClick(View view) {

    }


}
