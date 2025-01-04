package com.example.aptitude;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    private Context context;
    private List<Course> courseList;

    public CourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        // Set course name and description
        holder.courseNameTextView.setText(course.getName());
        holder.courseDescriptionTextView.setText(course.getDescription()); // Binding description

        // Set up click listener to open the CourseActivity
        holder.itemView.setOnClickListener(v -> {
            String courseId = course.getId();
            if (courseId == null) {
                Log.e("CourseAdapter", "Course ID is null for course: " + course.getName());
            }
            Intent intent = new Intent(context, CourseActivity.class);
            intent.putExtra("courseId", courseId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    // ViewHolder pattern to optimize performance
    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameTextView;
        TextView courseDescriptionTextView;  // Added for description

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.course_name_textview);
            courseDescriptionTextView = itemView.findViewById(R.id.course_description_textview);  // Initialized description TextView
        }
    }


}
