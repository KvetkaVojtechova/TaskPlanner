package com.flowertech.taskplanner;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class TaskListAdapter extends ListAdapter<TaskEntity, TaskViewHolder> {

    public TaskListAdapter(@NonNull DiffUtil.ItemCallback<TaskEntity> diffCallBack) {
        super(diffCallBack);
    }

    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TaskViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskEntity current = getItem(position);
        holder.bind(current);
    }

    static class TaskDiff extends DiffUtil.ItemCallback<TaskEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull TaskEntity oldItem, @NonNull TaskEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskEntity oldItem, @NonNull TaskEntity newItem) {
            return oldItem.title.equals(newItem.title);
        }
    }
}
