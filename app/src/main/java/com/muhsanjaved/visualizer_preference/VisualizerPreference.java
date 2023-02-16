package com.muhsanjaved.visualizer_preference;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.muhsanjaved.visualizer_preference.AudioVisuals.AudioInputReader;
import com.muhsanjaved.visualizer_preference.AudioVisuals.VisualizerView;


public class VisualizerPreference extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        mVisualizerView = findViewById(R.id.activity_visualizer);
        setupSharePreferences();
        setupPermissions();
    }

    private void setupSharePreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mVisualizerView.setShowBass(sharedPreferences.getBoolean(getString(R.string.pref_show_bass_key), getResources().getBoolean(R.bool.pref_show_bass_default)));

        mVisualizerView.setShowMid(sharedPreferences.getBoolean(getString(R.string.pref_show_min_range_key), getResources().getBoolean(R.bool.pref_show_min_range_default)));

        mVisualizerView.setShowTreble(sharedPreferences.getBoolean(getString(R.string.pref_show_treble_key), getResources().getBoolean(R.bool.pref_show_treble_default)));

       // mVisualizerView.setMinSizeScale(1);
        loadSizeFromSharePreferences(sharedPreferences);
        loadColorFromPreference(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_show_bass_key))) {
            mVisualizerView.setShowBass(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_bass_default)));
        } else if (key.equals(getString(R.string.pref_show_min_range_key))) {
            mVisualizerView.setShowMid(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_min_range_default)));
        } else if (key.equals(getString(R.string.pref_show_treble_key))) {
            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_show_treble_default)));
        } else if (key.equals(getString(R.string.pref_color_key))) {
            loadColorFromPreference(sharedPreferences);
        } else if (key.equals(getString(R.string.pref_size_key))) {
            loadSizeFromSharePreferences(sharedPreferences);
        }

    }

    private void loadColorFromPreference(SharedPreferences sharedPreferences){
        mVisualizerView.setColor(sharedPreferences.getString(getString(R.string.pref_color_key),
                getString(R.string.pref_color_red_value)));
    }

    private void loadSizeFromSharePreferences(SharedPreferences sharedPreferences){
        float minSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key),
                getString(R.string.pref_size_default)));
        mVisualizerView.setMinSizeScale(minSize);
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.visualizher_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            Intent startSettingActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingActivity);
        }

        return super.onOptionsItemSelected(item);
    }


     // Below this point is code you do not need to modify; it deals with permissions and starting/cleaning up the AudioInputReader
     // onPause Cleanup audio stream
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaked!..
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();

                }
            }


        }
    }


}