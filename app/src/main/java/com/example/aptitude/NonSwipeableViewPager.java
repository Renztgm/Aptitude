package com.example.aptitude;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.FrameLayout;

public class NonSwipeableViewPager extends FrameLayout {

    private ViewPager2 viewPager2;
    private boolean swipeEnabled = false;

    public NonSwipeableViewPager(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NonSwipeableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Initialize ViewPager2 and add it as a child view
        viewPager2 = new ViewPager2(context);
        addView(viewPager2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !swipeEnabled || super.onInterceptTouchEvent(ev);  // Prevent swipe if swipeEnabled is false
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !swipeEnabled || super.onTouchEvent(ev);  // Prevent touch events if swipeEnabled is false
    }

    // Set swipe enabled state
    public void setSwipeEnabled(boolean enabled) {
        this.swipeEnabled = enabled;
    }

    // Get the ViewPager2 instance
    public ViewPager2 getViewPager() {
        return viewPager2;
    }
}
