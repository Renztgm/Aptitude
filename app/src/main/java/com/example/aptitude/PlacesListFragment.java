package com.example.aptitude;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PlacesListFragment extends Fragment {

    private RecyclerView placesRecyclerView;
    private PlacesAdapter placesAdapter;
    private List<Place> placesList;

    public PlacesListFragment(List<Place> placesList) {
        this.placesList = placesList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);

        placesRecyclerView = rootView.findViewById(R.id.places_recycler_view);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        placesAdapter = new PlacesAdapter(placesList);
        placesRecyclerView.setAdapter(placesAdapter);

        return rootView;
    }
}
