package com.flowertech.taskplanner;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class CategoryListAdapter extends ListAdapter<Category, CategoryViewHolder> {

    private OnItemClickListener listener;

    public CategoryListAdapter(@NonNull DiffUtil.ItemCallback<Category> diffCallBack) {
        super(diffCallBack);
    }

    //creates CategoryViewHolder
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CategoryViewHolder.create(parent, listener);
    }

    //bind current task
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category current = getItem(position);
        holder.bind(current);
    }

    //returns category's position
    public Category getCategoryAt(int position){
        Category current = getItem(position);
        return current;
    }

    static class CategoryDiff extends DiffUtil.ItemCallback<Category> {

        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.title.equals(newItem.title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(CategoryListAdapter.OnItemClickListener listener) { this.listener = listener; }
}
