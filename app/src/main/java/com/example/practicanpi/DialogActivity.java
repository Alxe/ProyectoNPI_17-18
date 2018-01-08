package com.example.practicanpi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ai.api.android.AIConfiguration;
import ai.api.ui.AIDialog;

public class DialogActivity extends NpiActivity {
    /**
     * Dialogo reuitilizable para contactar con Dialogflow
     */
    private AIDialog aiDialog;

    private DialogInputFragment dialogInputFragment;
    private DialogResponseFragment dialogResponseFragment;

    /**
     * Comprueba y, de no existir, solicita permisos de audio al usuario
     * @return <code>true</code> si los permisos  han sido concedidos, <code>false</code> en caso contrario
     */
    private boolean checkAndRequestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Si se necesita pemisos, mostrar una explicación
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
                Toast.makeText(getApplicationContext(), R.string.need_mic_permission, Toast.LENGTH_SHORT).show();

            // Solicita los permisos
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.RECORD_AUDIO },0);
        }

        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        // Toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fragments
        final FragmentManager fm = getSupportFragmentManager();
        dialogInputFragment = (DialogInputFragment) fm.findFragmentById(R.id.fragment_dialog_input);
        dialogResponseFragment = (DialogResponseFragment) fm.findFragmentById(R.id.fragment_dialog_response);

        // API.ai (Dialogflow)
        final AIConfiguration aiConfig = AIProvider.getInstance().getAiConfig(this);
        aiDialog = new AIDialog(DialogActivity.this, aiConfig);
        aiDialog.setResultsListener(dialogResponseFragment);

        // FAB (Floating Action Button)
        final FloatingActionButton fab = findViewById(R.id.fab);
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
}
