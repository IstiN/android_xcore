package by.istin.android.xcore.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.utils.ContentUtils;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentUtils.getEntities(this, SimpleEntity.class, null);
        startActivity(new Intent(this, WearableActivity.class));
        finish();
    }
}
