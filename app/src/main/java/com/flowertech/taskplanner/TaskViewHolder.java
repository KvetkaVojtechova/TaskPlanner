package com.flowertech.taskplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class TaskViewHolder extends RecyclerView.ViewHolder {

    private final TextView mTaskTitle;
    private final TextView mTaskDesc;
    private final TextView mTaskDueDate;
    private final TextView mTaskCategory;
    private final ImageView mTaskState;

    private TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        mTaskTitle = itemView.findViewById(R.id.text_view_title);
        mTaskDesc = itemView.findViewById(R.id.text_view_description);
        mTaskDueDate = itemView.findViewById(R.id.text_view_due_date);
        mTaskCategory = itemView.findViewById(R.id.text_view_category);
        mTaskState = itemView.findViewById(R.id.image_view_state);
    }

    public void bind(TaskEntity task) {
        mTaskTitle.setText(task.title);
        mTaskDesc.setText(task.description);
        if (task.dueDate != null) {
            mTaskDueDate.setText(task.dueDate.toString());
        }
        //mTaskState.setImageDrawable();
    }

    static TaskViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new TaskViewHolder(view);
    }
}
