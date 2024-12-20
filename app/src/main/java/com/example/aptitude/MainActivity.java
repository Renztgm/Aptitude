package com.example.aptitude;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set up ViewPager2 with an adapter
        viewPager.setAdapter(new FragmentAdapter(this));

        viewPager.setUserInputEnabled(false);// disabling swiping

        // Link BottomNavigationView with ViewPager2
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.tab1) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (item.getItemId() == R.id.tab2) {
                viewPager.setCurrentItem(1, true);
                return true;
            } else if (item.getItemId() == R.id.tab3) {
                viewPager.setCurrentItem(2, true);
                return true;
            } else if (item.getItemId() == R.id.tab4) {
                viewPager.setCurrentItem(3, true);
                return true;
            } else if (item.getItemId() == R.id.tab5) {
                viewPager.setCurrentItem(4, true);
                return true;
            }
            return false;
        });

        // Link ViewPager2 swipe events to BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.tab1);
                } else if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.tab2);
                } else if (position == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.tab3);
                }
                 else if (position == 3) {
                    bottomNavigationView.setSelectedItemId(R.id.tab4);
                }
                else if (position == 4) {
                    bottomNavigationView.setSelectedItemId(R.id.tab5);
                }

            }
        });

    }

    private static class FragmentAdapter extends FragmentStateAdapter {

        public FragmentAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new Tab1Fragment();
            } else if (position == 1) {
                return new Tab2Fragment();
            } else if (position == 2) {
                return new Tab3Fragment();
            } else if (position == 3) {
                return new Tab4Fragment();
            } else if (position == 4) {
                return new Tab5Fragment();
            } else {
                return new Tab1Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 5; // Number of tabs
        }
    }
}
