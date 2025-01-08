package com.example.aptitude;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.ViewHolder> {

    private List<Flashcard> flashcardList;
    private String mode;

    public FlashcardAdapter(List<Flashcard> flashcardList, String mode) {
        this.flashcardList = flashcardList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flashcard flashcard = flashcardList.get(position);
        holder.tvQuestion.setText(flashcard.getQuestion());
        holder.tvAnswer.setText(flashcard.getAnswer());

        if (mode.equals("answer")) {
            holder.tvAnswer.setVisibility(View.VISIBLE);
            holder.tvQuestion.setVisibility(View.GONE);
        } else {
            holder.tvAnswer.setVisibility(View.GONE);
            holder.tvQuestion.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            ObjectAnimator flipIn = ObjectAnimator.ofFloat(holder.itemView, "rotationY", 90f, 0f);
            flipIn.setDuration(500);
            flipIn.start();

            if (holder.tvAnswer.getVisibility() == View.GONE) {
                holder.tvAnswer.setVisibility(View.VISIBLE);
                holder.tvQuestion.setVisibility(View.GONE);
            } else {
                holder.tvAnswer.setVisibility(View.GONE);
                holder.tvQuestion.setVisibility(View.VISIBLE);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Flashcard cardToDelete = flashcardList.get(position);

            db.collection("flashcards").document(userId).collection("userFlashcards")
                    .document(cardToDelete.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        flashcardList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(v.getContext(), "Flashcard deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error deleting flashcard", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
