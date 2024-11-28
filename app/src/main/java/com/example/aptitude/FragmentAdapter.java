package com.example.aptitude;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Tab1Fragment(); // Fragment for Tab 1
            case 1:
                return new Tab2Fragment(); // Fragment for Tab 2
            case 2:
                return new Tab3Fragment(); // Fragment for Tab 3
            default:
                return new Tab1Fragment(); // Default to Tab 1 if an invalid position is provided
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of fragments (tabs)
    }
}
