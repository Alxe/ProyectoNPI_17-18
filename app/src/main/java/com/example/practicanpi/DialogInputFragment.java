package com.example.practicanpi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

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

        for(int i = 0; i < 10; ++i) {
            adapter.add(String.format(Locale.ENGLISH, "Item %d", i));
        }

        list = v.findViewById(R.id.dialog_input_list);
        list.setAdapter(adapter);


        return v;
    }

}
