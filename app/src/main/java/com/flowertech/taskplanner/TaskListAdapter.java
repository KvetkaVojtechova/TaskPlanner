package com.flowertech.taskplanner;

import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class TaskListAdapter extends ListAdapter<TaskList, TaskViewHolder> {

    private OnItemClickListener listener;

    public TaskListAdapter(@NonNull DiffUtil.ItemCallback<TaskList> diffCallBack) {
        super(diffCallBack);
    }

    //creates TaskViewHolder
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TaskViewHolder.create(parent, listener);
    }

    //bind current taskList
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskList current = getItem(position);
        holder.bind(current);
    }

    //returns task's position
    public TaskList getTaskAt(int position){
        TaskList current = getItem(position);
        return current;
    }

    static class TaskDiff extends DiffUtil.ItemCallback<TaskList> {

        @Override
        public boolean areItemsTheSame(@NonNull TaskList oldItem, @NonNull TaskList newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskList oldItem, @NonNull TaskList newItem) {
            return oldItem.title.equals(newItem.title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
