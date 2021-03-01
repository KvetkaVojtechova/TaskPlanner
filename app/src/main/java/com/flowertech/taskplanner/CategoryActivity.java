package com.flowertech.taskplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CategoryActivity extends AppCompatActivity {
    private CategoryViewModel mCategoryViewModel;
    public static final int NEW_CATEGORY_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_CATEGORY_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //Set tasks_page selected
        bottomNavigationView.setSelectedItemId(R.id.categories_page);
        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tasks_page:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.categories_page:
                        return true;
                }
                return false;
            }
        });

        //Initialize and assign variable
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final CategoryListAdapter adapter = new CategoryListAdapter(new CategoryListAdapter.CategoryDiff());
        //set adapter to recyclerview
        recyclerView.setAdapter(adapter);

        mCategoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        mCategoryViewModel.getAllCategories().observe(this, categoryEntities -> {
            adapter.submitList(categoryEntities);
        });

        //when floating button is clicked, start NewTaskActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryActivity.this, NewCategoryActivity.class);
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
                Toast.makeText(CategoryActivity.this, R.string.category_deleted, Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);

        //when category is clicked, start EditCategoryActivity
        adapter.setOnItemClickListener(new CategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Category category = adapter.getCategoryAt(position);
                Intent intent = new Intent(CategoryActivity.this, EditCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditCategoryActivity.EDIT_CATEGORY, category);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_CATEGORY_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    //if RESULT_OK in NewCategoryActivity, then insert category into CategoryViewModel,
    //otherwise Toast
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_CATEGORY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Category category = (Category) bundle.getSerializable(NewCategoryActivity.EXTRA_CATEGORY);
            mCategoryViewModel.insert(category);
        } else if(requestCode == EDIT_CATEGORY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            Category category = (Category) bundle.getSerializable(EditCategoryActivity.EDIT_CATEGORY);
            mCategoryViewModel.update(category);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved_cat,
                    Toast.LENGTH_LONG).show();
        }
    }

    //creates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.category_menu, menu);
        return true;
    }

    //put deleteAllTasks into menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_categories:
                mCategoryViewModel.deleteAllCategories();
                Toast.makeText(this, R.string.all_categories_deleted, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}