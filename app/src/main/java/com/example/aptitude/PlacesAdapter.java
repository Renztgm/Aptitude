package com.example.aptitude;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private List<Place> placesList;
    private OnPlaceClickListener onPlaceClickListener;

    public PlacesAdapter(List<Place> placesList, OnPlaceClickListener onPlaceClickListener) {
        this.placesList = placesList;
        this.onPlaceClickListener = onPlaceClickListener;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = placesList.get(position);
        holder.nameTextView.setText(place.getName());
        holder.addressTextView.setText(place.getAddress());
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.place_name);
            addressTextView = itemView.findViewById(R.id.place_address);
            itemView.setOnClickListener(v -> onPlaceClickListener.onPlaceClick(getAdapterPosition()));
        }
    }

    public interface OnPlaceClickListener {
        void onPlaceClick(int position);
    }
}
