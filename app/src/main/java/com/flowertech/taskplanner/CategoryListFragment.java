package com.flowertech.taskplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryListFragment extends Fragment {
    private CategoryViewModel mCategoryViewModel;
    public static final int NEW_CATEGORY_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_CATEGORY_ACTIVITY_REQUEST_CODE = 2;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category_list, container, false);

        //Initialize and assign variable
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);

        final CategoryListAdapter adapter = new CategoryListAdapter(new CategoryListAdapter.CategoryDiff());
        //set adapter to recyclerview
        recyclerView.setAdapter(adapter);

        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        mCategoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categoryEntities -> {
            adapter.submitList(categoryEntities);
        });

        //when floating button is clicked, start NewCategoryActivity
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(), NewCategoryActivity.class);
            startActivityForResult(intent, NEW_CATEGORY_ACTIVITY_REQUEST_CODE);
        });

        //deletes category on swipe to the right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mCategoryViewModel.delete(adapter.getCategoryAt(viewHolder.getAdapterPosition()));
                Toast.makeText(v.getContext(), R.string.category_deleted, Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);

        //when category is clicked, start EditCategoryActivity
        adapter.setOnItemClickListener(new CategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Category category = adapter.getCategoryAt(position);
                Intent intent = new Intent(v.getContext(), EditCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditCategoryActivity.EDIT_CATEGORY, category);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_CATEGORY_ACTIVITY_REQUEST_CODE);
            }
        });

        return v;
    }

    //if RESULT_OK in NewCategoryActivity, then insert category into CategoryViewModel,
    //else if RESULT_OK in EditCategoryActivity, then update category into CategoryViewModel,
    //otherwise Toast
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_CATEGORY_ACTIVITY_REQUEST_CODE && resultCode == NewCategoryActivity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Category category = (Category) bundle.getSerializable(NewCategoryActivity.EXTRA_CATEGORY);
            mCategoryViewModel.insert(category);
        } else if(requestCode == EDIT_CATEGORY_ACTIVITY_REQUEST_CODE && resultCode == EditCategoryActivity.RESULT_OK){
            Bundle bundle = data.getExtras();
            Category category = (Category) bundle.getSerializable(EditCategoryActivity.EDIT_CATEGORY);
            mCategoryViewModel.update(category);
        } else {
            Toast.makeText(
                    getContext(),
                    R.string.empty_not_saved_cat,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //put deleteAllTasks into menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_categories:
                mCategoryViewModel.deleteAllCategories();
                Toast.makeText(getContext(), R.string.all_categories_deleted, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
