package com.shkmishra.lyrically;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class PreferenceActivity extends AppCompatActivity {

    boolean isMusicPlaying;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this, PreferenceTrigger.class);
        stopService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, PreferenceTrigger.class);
        stopService(intent);
        if (isMusicPlaying) {
            Intent intent1 = new Intent(this, LyricsService.class);
            startService(intent1);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        isMusicPlaying = audioManager.isMusicActive();
        checkDrawOverlayPermission();
    }

    public void checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 69);
            } else {
                Intent intent = new Intent(this, PreferenceTrigger.class);
                startService(intent);

                final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.cancel(26181317);
                Intent intent1 = new Intent(this, LyricsService.class);
                stopService(intent1);

            }
        } else {
            Intent intent = new Intent(this, PreferenceTrigger.class);
            startService(intent);

            final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyManager.cancel(26181317);
            Intent intent1 = new Intent(this, LyricsService.class);
            stopService(intent1);

        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public void onResume() {
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!(key.equals("triggerOffset") || key.equals("triggerWidth") || key.equals("triggerHeight"))) {
                Intent intent = new Intent(getActivity(), PreferenceTrigger.class);
                getActivity().stopService(intent);
                getActivity().startService(intent);
            }
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }
}
