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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private TaskViewModel mTaskViewModel;
    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TASK_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TaskListAdapter adapter = new TaskListAdapter(new TaskListAdapter.TaskDiff());
        recyclerView.setAdapter(adapter);

        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        mTaskViewModel.getAllTasks().observe(this, taskEntities -> {
            adapter.submitList(taskEntities);
        });

        //when floating button is clicked, start NewTaskActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
            startActivityForResult(intent, NEW_TASK_ACTIVITY_REQUEST_CODE);
        });

        //deletes task on swipe to the right
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mTaskViewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(recyclerView);

        //when task is clicked, start EditTaskActivity
        adapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Task task = adapter.getTaskAt(position);
                Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditTaskActivity.EDIT_TASK, task);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_TASK_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    //if RESULT_OK in NewTaskActivity, then insert task into TaskViewModel,
    //otherwise Toast
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Task task = (Task) bundle.getSerializable(NewTaskActivity.EXTRA_TASK);
            mTaskViewModel.insert(task);
        } else if(requestCode == EDIT_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            Task task = (Task) bundle.getSerializable(EditTaskActivity.EDIT_TASK);
            mTaskViewModel.update(task);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    //creates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //put deleteAllTasks into menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_tasks:
                mTaskViewModel.deleteAllTasks();
                Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}