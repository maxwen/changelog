package com.bytehamster.changelog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import org.omnirom.omnichange.R;

public class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.prefs);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        findPreference("clear_cache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                clearCache();
                return false;
            }
        });

        findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(R.string.about);
                alert.setMessage(Html.fromHtml(getString(R.string.about_message)));
                alert.setPositiveButton(android.R.string.ok, null);
                Dialog d = alert.show();
                ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                return true;
            }
        });
    }

    private void clearCache() {
        Toast.makeText(getContext(), R.string.cleared_cache, Toast.LENGTH_LONG).show();
        ChangeCacheDatabase database = new ChangeCacheDatabase(getContext());
        database.clearCache();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putLong("cache_lastrefresh", 0).apply();
    }
}