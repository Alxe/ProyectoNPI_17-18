package com.example.practicanpi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ai.api.android.AIConfiguration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_button_es).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoIndexActivity(AIConfiguration.SupportedLanguages.Spanish);
            }
        });

        findViewById(R.id.main_button_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoIndexActivity(AIConfiguration.SupportedLanguages.English);
            }
        });
    }

    private void gotoIndexActivity(AIConfiguration.SupportedLanguages languages) {
        NpiApplication app = (NpiApplication) getApplication();
        app.setAiLanguage(languages);

        Intent intent = new Intent(getBaseContext(), IndexActivity.class);
        startActivity(intent);
    }
}
