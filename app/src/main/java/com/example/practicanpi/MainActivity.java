package com.example.practicanpi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ai.api.GsonFactory;
import ai.api.android.AIConfiguration;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.ui.AIDialog;

public class MainActivity extends AppCompatActivity implements AIDialog.AIDialogListener {
    private Gson gson = new GsonFactory().getGson();

    private AIDialog aiDialog;
    private TextView textView;

    private DialogInputFragment dialogInputFragment;
    private DialogResponseFragment dialogResponseFragment;

    private boolean checkAndRequestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // If  an explanation is required, show it
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
                Toast.makeText(getApplicationContext(), "Need microphone to record", Toast.LENGTH_SHORT).show();

            // Request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.RECORD_AUDIO },0);
        }

        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fragments
        FragmentManager fm = getSupportFragmentManager();
        dialogInputFragment = (DialogInputFragment) fm.findFragmentById(R.id.fragment_dialog_input);
        dialogResponseFragment = (DialogResponseFragment) fm.findFragmentById(R.id.fragment_dialog_response);

        // API.ai (Dialogflow)
        final AIConfiguration config = new AIConfiguration("1973329acf964ea48e5d08e04d2c08a0",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiDialog = new AIDialog(this, config);
        aiDialog.setResultsListener(this);

        // FAB (Floating Action Button)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestRecordAudioPermission()) aiDialog.showAndListen();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(final                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String responseText = response.getResult().getFulfillment().getSpeech();

                dialogResponseFragment.setText(responseText);
            }
        });
    }

    @Override
    public void onError(AIError error) {
        Log.e(getClass().getSimpleName(), "Error on listen");
    }

    @Override
    public void onCancelled() {
        Log.i(getClass().getSimpleName(), "Canceled recording");
    }
}
