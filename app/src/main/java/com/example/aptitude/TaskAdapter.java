package com.example.aptitude;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> tasks;
    private CollectionReference tasksCollection;

    public TaskAdapter(ArrayList<Task> tasks, CollectionReference tasksCollection) {
        this.tasks = tasks;
        this.tasksCollection = tasksCollection;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        Log.d("TaskAdapter", "Binding task at position: " + position);

        if (holder.dateTextView == null) {
            Log.e("TaskAdapter", "dateTextView is null!");
        } else {
            holder.dateTextView.setText(task.getDate());  // Set the date text here
        }

        holder.taskDescription.setText(task.getTaskDescription());
        holder.completedCheckBox.setChecked(task.isCompleted());

        holder.completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            // Update Firestore
            DocumentReference taskRef = tasksCollection.document(task.getId());
            taskRef.update("completed", isChecked);
        });
    }






    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskDescription;
        public TextView dateTextView;  // Make sure you have this for date
        public CheckBox completedCheckBox;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            dateTextView = itemView.findViewById(R.id.dateTextView);  // Bind the TextView
            completedCheckBox = itemView.findViewById(R.id.completedCheckBox);
        }
    }




}
