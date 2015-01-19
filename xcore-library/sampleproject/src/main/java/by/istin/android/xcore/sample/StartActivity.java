package by.istin.android.xcore.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by Uladzimir_Klyshevich on 1/19/2015.
 */
public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onSimpleClick(View view) {
        startActivity(new Intent(this, SimpleActivity.class));
    }

    public void onAdvancedClick(View view) {
        startActivity(new Intent(this, AdvancedActivity.class));
    }
}
