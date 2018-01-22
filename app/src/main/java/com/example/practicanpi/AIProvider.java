package com.example.practicanpi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ai.api.android.AIConfiguration;

/**
 * Clase <i>Singleton</i> que gestiona la configuración de DialogFlow
 */
class AIProvider {

    static private final AIProvider instance = new AIProvider();

    static AIProvider getInstance() { return instance; }

    private AIProvider() {}
    // end singleton

    private final String AI_TOKEN = "1973329acf964ea48e5d08e04d2c08a0";

    /**
     * Dado un Contexto, proporciona una configuración basada en el idioma de preferencias
     * @param context Contexto de una aplicación o actividad
     * @return Objeto de onfiguración de Dialogflow
     */
    AIConfiguration getAiConfig(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences("NpiPrefs", Context.MODE_PRIVATE);

        final Locale locale = (prefs.contains("language"))
                ? new Locale(prefs.getString("language", null))
                : Locale.getDefault();

        AIConfiguration.SupportedLanguages aiLanguage = null;
        for(Map.Entry<Locale, AIConfiguration.SupportedLanguages> e : getLanguageMap().entrySet()) {
            if(e.getKey().equals(locale)) {
                aiLanguage = e.getValue();
                break;
            }
        }

        if(aiLanguage == null) {
            Log.e(getClass().getSimpleName(), "aiLanguage is null");
        }

        return new AIConfiguration(AI_TOKEN, aiLanguage, AIConfiguration.RecognitionEngine.System);
    }

    private Map<Locale, AIConfiguration.SupportedLanguages> getLanguageMap() {
        final Map<Locale, AIConfiguration.SupportedLanguages> map = new HashMap<>();

        map.put(new Locale("es"), AIConfiguration.SupportedLanguages.Spanish);
        map.put(new Locale("en"), AIConfiguration.SupportedLanguages.English);

        return map;
    }
}
