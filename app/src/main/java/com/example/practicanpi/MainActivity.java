package com.example.practicanpi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Locale;

import ai.api.android.AIConfiguration;

public class MainActivity extends NpiActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_button_es).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoIndexActivity(new Locale("es"));
            }
        });

        findViewById(R.id.main_button_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoIndexActivity(new Locale("en"));
            }
        });
    }

    private void gotoIndexActivity(Locale locale) {
        final SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_key), MODE_PRIVATE);

        if(locale == null) {
            locale = Locale.getDefault();
        }

        // Save default locale
        prefs.edit()
            .putString(getString(R.string.prefs_language_key), locale.getLanguage())
            .apply();

        // Launch Activity
        Intent intent = new Intent(getBaseContext(), IndexActivity.class);
        startActivity(intent);
    }
}
