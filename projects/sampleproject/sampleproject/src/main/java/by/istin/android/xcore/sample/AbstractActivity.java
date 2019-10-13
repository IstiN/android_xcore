package by.istin.android.xcore.sample;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by IstiN on 13.11.13.
 */
public abstract class AbstractActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, createFragment())
                    .commit();
        }
    }

    protected abstract Fragment createFragment();

}
