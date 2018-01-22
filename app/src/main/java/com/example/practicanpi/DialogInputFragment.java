package com.example.practicanpi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ai.api.AIServiceException;
import ai.api.model.AIResponse;
import ai.api.ui.AIDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogInputFragment extends Fragment {

    private ListView list;
    private ArrayAdapter<String> adapter;

    public DialogInputFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_dialog_input, container);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        // Mensajes mostrados en la lista: Lo ideal sería mostrar posibles respuestas al agente de
        // Dialogflow, pero por complejidad no está planteado en la práctica
        adapter.addAll(
                getString(R.string.dialog_input_fragment_item0),
                getString(R.string.dialog_input_fragment_item1),
                getString(R.string.dialog_input_fragment_item2),
                getString(R.string.dialog_input_fragment_item3),
                getString(R.string.dialog_input_fragment_item4)
        );

        list = v.findViewById(R.id.dialog_input_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final String clickedItem = (String) parent.getItemAtPosition(position);

                Toast.makeText(
                        getActivity().getBaseContext(),
                        String.format(getString(R.string.fragment_dialog_input_itemclick), clickedItem),
                        Toast.LENGTH_LONG)
                     .show();

                // TODO: Implementar la interacción con Dialogflow al pulsar un botón
//                Runnable r = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            DialogActivity parentActivity = (DialogActivity) getActivity();
//
//                            AIResponse response = parentActivity.getAiDialog().textRequest(clickedItem);
//                        } catch(AIServiceException aise) {
//                            Log.e(DialogInputFragment.class.getSimpleName(), "Error on OnItemClick: " + aise.getMessage());
//                        }
//                    }
//                };
//
//                new Thread(r).start();
            }
        });

        return v;
    }

}
