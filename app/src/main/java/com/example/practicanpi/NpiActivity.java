package com.example.practicanpi;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

/**
 * Clase base para las actividades de la aplicación
 *
 * Su principal uso es cambiar el idioma base, basandonse en las preferencias de usuario
 */
abstract class NpiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_key), MODE_PRIVATE);
        if(prefs.contains(getString(R.string.prefs_language_key))) {
            final String localeString = prefs.getString(getString(R.string.prefs_language_key), "");

            final Resources resources = getResources();
            final Configuration config = resources.getConfiguration();
            final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            config.setLocale(new Locale(localeString));
            resources.updateConfiguration(config, displayMetrics);


        }
    }
}
