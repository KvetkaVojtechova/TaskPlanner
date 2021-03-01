package com.flowertech.taskplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder{

    private final TextView mCategoryAbbr;
    private final TextView mCategoryTitle;
    private final TextView mCategoryDescription;

    private CategoryViewHolder(@NonNull View itemView, CategoryListAdapter.OnItemClickListener listener) {
        super(itemView);
        mCategoryAbbr = itemView.findViewById(R.id.text_view_abbr);
        mCategoryTitle = itemView.findViewById(R.id.text_view_title);
        mCategoryDescription = itemView.findViewById(R.id.text_view_description);

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

    public void bind(Category category) {
        mCategoryTitle.setText(category.title);
        mCategoryDescription.setText(category.description);
        mCategoryAbbr.setText(category.abbr);
    }

    static CategoryViewHolder create(ViewGroup parent, CategoryListAdapter.OnItemClickListener listener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recyclerview_item, parent, false);
        return new CategoryViewHolder(view, listener);
    }
}
