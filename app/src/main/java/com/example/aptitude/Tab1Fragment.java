package com.example.aptitude;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Tab1Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        // Find the button in the layout
        Button openActivityButton = rootView.findViewById(R.id.search_button);

        // Set up a click listener for the button
        openActivityButton.setOnClickListener(v -> {
            // Create an intent to open the new activity
            Intent intent = new Intent(getActivity(), MapsActivity  .class);
            startActivity(intent);
        });

        // Account button to open AccountActivity
        Button accountButton = rootView.findViewById(R.id.account); // Button ID from XML
        accountButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        return rootView;
    }
}
