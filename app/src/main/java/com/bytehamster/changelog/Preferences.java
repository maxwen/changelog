package com.bytehamster.changelog;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import org.omnirom.omnichange.R;

public class Preferences extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
            finish();
        }
        return true;
    }
}
