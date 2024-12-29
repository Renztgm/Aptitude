package com.example.aptitude;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsetsController;
import android.view.WindowInsets;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove the action bar (top navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        // Initialize ViewPager and BottomNavigationView
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView); // Correct reference
        FloatingActionButton fab = findViewById(R.id.fab);

        // Set up ViewPager2 with an adapter
        viewPager.setAdapter(new FragmentAdapter(this));

        viewPager.setUserInputEnabled(false); // Disabling swiping

        // Link BottomNavigationView menu with ViewPager2
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
            } else {
                return false;
            }
        });


        // Link ViewPager2 swipe events to BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update BottomNavigationView's selected item based on the current ViewPager position
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.tab1);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.tab2);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.tab3);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.tab4);
                        break;
                    default:
                        bottomNavigationView.setSelectedItemId(R.id.tab1);
                        break;
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
            switch (position) {
                case 0:
                    return new Tab1Fragment();
                case 1:
                    return new Tab2Fragment();
                case 2:
                    return new Tab3Fragment();
                case 3:
                    return new Tab4Fragment();
                default:
                    return new Tab1Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 5; // Number of tabs
        }
    }
}
