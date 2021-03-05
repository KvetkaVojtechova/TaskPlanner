package com.flowertech.taskplanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskListFragment extends Fragment {
    private TaskViewModel mTaskViewModel;
    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TASK_ACTIVITY_REQUEST_CODE = 2;

    public TaskListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_task_list, container, false);

        //Initialize and assign variable
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        recyclerView.setHasFixedSize(true);

        final TaskListAdapter adapter = new TaskListAdapter(new TaskListAdapter.TaskDiff());
        //set adapter to recyclerview
        recyclerView.setAdapter(adapter);

        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        mTaskViewModel.getAllTaskList().observe(getViewLifecycleOwner(), taskEntities -> {
            adapter.submitList(taskEntities);
        });

        //when floating button is clicked, start NewTaskActivity
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(v.getContext(), NewTaskActivity.class);
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
                TaskList taskList = adapter.getTaskAt(viewHolder.getAdapterPosition());
                mTaskViewModel.getTask(taskList.id).observe(getViewLifecycleOwner(), task -> {
                    if(task != null){
                        mTaskViewModel.delete(task);
                        Toast.makeText(v.getContext(), R.string.task_deleted, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);

        //when task is clicked, start EditTaskActivity
        adapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TaskList taskList = adapter.getTaskAt(position);
                Intent intent = new Intent(v.getContext(), EditTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(EditTaskActivity.EDIT_TASK, taskList.id);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_TASK_ACTIVITY_REQUEST_CODE);
            }
        });

        return v;
    }

    /*//if RESULT_OK in NewTaskActivity, then insert task into TaskViewModel,
    //else if RESULT_OK in EditTaskActivity, then update task into TaskViewModel,
    //otherwise Toast
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == NewTaskActivity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Task task = (Task) bundle.getSerializable(NewTaskActivity.EXTRA_TASK);
            mTaskViewModel.insert(task);
        } else if(requestCode == EDIT_TASK_ACTIVITY_REQUEST_CODE && resultCode == EditTaskActivity.RESULT_OK){
            Bundle bundle = data.getExtras();
            Task task = (Task) bundle.getSerializable(EditTaskActivity.EDIT_TASK);
            mTaskViewModel.update(task);
        } else {
            Toast.makeText(
                    getContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //put deleteAllTasks into menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_tasks:
                mTaskViewModel.deleteAllTasks();
                Toast.makeText(getContext(), R.string.all_tasks_deleted, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}