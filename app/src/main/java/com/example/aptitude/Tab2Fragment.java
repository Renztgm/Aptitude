package com.example.aptitude;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tab2Fragment extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private EditText taskDescriptionEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskDescriptionEditText = view.findViewById(R.id.taskDescription);

        // Initialize task list
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("tasks"));

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        // Load tasks from Firestore
        loadTasks();

        // Add a new task
        view.findViewById(R.id.addTaskButton).setOnClickListener(v -> addTask());
        TextView dateTextView = view.findViewById(R.id.dateTextView);  // Make sure to use the correct ID

// Set the current date with the suffix
        dateTextView.setText(getFormattedDate());


        return view;
    }

    private void loadTasks() {
        CollectionReference tasksCollection = firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("tasks");

        tasksCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                taskList.clear();
                for (DocumentSnapshot document : querySnapshot) {
                    Task taskItem = document.toObject(Task.class);
                    taskList.add(taskItem);
                }
                taskAdapter.notifyDataSetChanged();
                Log.d("Tab2Fragment", "Task list size: " + taskList.size());
            } else {
                Toast.makeText(getContext(), "Failed to load tasks.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addTask() {
        String taskDescription = taskDescriptionEditText.getText().toString().trim();

        if (!taskDescription.isEmpty()) {
            // Get the formatted date with the correct suffix
            String currentDate = getFormattedDate();

            // Create the new task with description and formatted date
            Task newTask = new Task(taskDescription);

            // Add task to Firestore
            firestore.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("tasks")
                    .add(newTask)
                    .addOnSuccessListener(documentReference -> {
                        // Set the document ID after it's successfully added
                        newTask.setId(documentReference.getId());
                        taskDescriptionEditText.setText("");
                        loadTasks();  // Reload tasks after adding
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to add task.", Toast.LENGTH_SHORT).show();
                    });

        } else {
            Toast.makeText(getContext(), "Please enter a task description.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add the correct suffix to the date
    private String getFormattedDate() {
        int dayOfMonth = new Date().getDate();
        String suffix = "th"; // Default suffix

        if (dayOfMonth % 10 == 1 && dayOfMonth != 11) {
            suffix = "st";
        } else if (dayOfMonth % 10 == 2 && dayOfMonth != 12) {
            suffix = "nd";
        } else if (dayOfMonth % 10 == 3 && dayOfMonth != 13) {
            suffix = "rd";
        }

        return dayOfMonth + suffix;
    }

    // In your addTask method, replace the current date code with:
    String currentDate = getFormattedDate();



}
