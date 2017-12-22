package com.example.practicanpi;


import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.ui.AIDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogResponseFragment extends Fragment implements AIDialog.AIDialogListener, TextToSpeech.OnInitListener {

    private TextView responseText;
    private TextToSpeech tts;

    public DialogResponseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_response, container);

        responseText = v.findViewById(R.id.dialog_response_text);

        // TTS
        tts = new TextToSpeech(getActivity(), this);

        return v;
    }

    @Override
    public void onResult(AIResponse result) {
        final String responseStr = result.getResult().getFulfillment().getSpeech();

        responseText.setText(responseStr);

        // TODO: Text-to-Speech
        tts.speak(responseStr, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onError(AIError error) {
        Log.e(getClass().getSimpleName(),
                String.format("Error on listen: %s", error));
    }

    @Override
    public void onCancelled() {
        Log.i(getClass().getSimpleName(), "Canceled recording");
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.ERROR) {
            Log.e(DialogResponseFragment.class.getSimpleName(),
                    String.format("Error on TTS init"));
        } else {
            // TODO: Localisation
            tts.setLanguage(Locale.getDefault());
        }
    }
}
