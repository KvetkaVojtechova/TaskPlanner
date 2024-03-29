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

    private TaskViewHolder(@NonNull View itemView, TaskListAdapter.OnItemClickListener listener) {
        super(itemView);
        mTaskTitle = itemView.findViewById(R.id.text_view_title);
        mTaskDesc = itemView.findViewById(R.id.text_view_description);
        mTaskDueDate = itemView.findViewById(R.id.text_view_due_date);
        mTaskCategory = itemView.findViewById(R.id.text_view_category);
        mTaskState = itemView.findViewById(R.id.image_view_state);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    public void bind(TaskList task) {
        mTaskTitle.setText(task.title);
        mTaskDesc.setText(task.description);
        mTaskCategory.setText(task.abbreviation);
        if (task.dueDate != null) {
            String dueDate = DateConverters.DateToString(task.dueDate);
            mTaskDueDate.setText(dueDate);
        } else {
            mTaskDueDate.setText("");
        }

        if (task.state == State.created) {
            mTaskState.setImageResource(R.drawable.ic_round_created_24);
        } else if (task.state == State.inProgress) {
            mTaskState.setImageResource(R.drawable.ic_baseline_in_progress_24);
        } else {
            mTaskState.setImageResource(R.drawable.ic_baseline_closed_24);
        }

    }

    static TaskViewHolder create(ViewGroup parent, TaskListAdapter.OnItemClickListener listener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_recyclerview_item, parent, false);
        return new TaskViewHolder(view, listener);
    }
}
