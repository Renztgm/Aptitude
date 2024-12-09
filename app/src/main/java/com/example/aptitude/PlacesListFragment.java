package com.example.aptitude;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class PlacesListFragment extends Fragment {

    private List<Place> placesList;
    private RecyclerView recyclerView;
    private PlacesAdapter placesAdapter;
    private GoogleMap googleMap;

    public PlacesListFragment(List<Place> placesList) {
        this.placesList = placesList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);
        recyclerView = rootView.findViewById(R.id.places_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        placesAdapter = new PlacesAdapter(placesList, position -> {
            // Get the selected place from the list
            Place selectedPlace = placesList.get(position);
            if (getActivity() instanceof MapsActivity) {
                ((MapsActivity) getActivity()).moveToPlace(selectedPlace);
            }
        });
        recyclerView.setAdapter(placesAdapter);

        return rootView;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
