package com.example.practicanpi;

import android.app.Application;

import ai.api.android.AIConfiguration;

/**
 * Created by alejnp on 16/12/17.
 */

public class NpiApplication extends Application {
    public final String aiToken = "1973329acf964ea48e5d08e04d2c08a0";

    private AIConfiguration.SupportedLanguages aiLanguage;

    public AIConfiguration.SupportedLanguages getAiLanguage() {
        return aiLanguage;
    }

    public void setAiLanguage(AIConfiguration.SupportedLanguages aiLanguage) {
        this.aiLanguage = aiLanguage;
    }

    public String getAiToken() {
        return aiToken;
    }
}
