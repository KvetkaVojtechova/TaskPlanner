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

import org.jetbrains.annotations.NotNull;

public class TaskListFragment extends Fragment {
    private TaskViewModel mTaskViewModel;
    private final TaskListAdapter adapter = new TaskListAdapter(new TaskListAdapter.TaskDiff());
    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TASK_ACTIVITY_REQUEST_CODE = 2;
    private boolean isCreatedChecked = false;
    private boolean isInProgressChecked = false;
    private boolean isClosedChecked = false;

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

        //set adapter to recyclerview
        recyclerView.setAdapter(adapter);

        mTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        mTaskViewModel.filterTaskList(isCreatedChecked, isInProgressChecked, isClosedChecked).observe(getViewLifecycleOwner(), adapter::submitList);

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
        adapter.setOnItemClickListener(position -> {
            TaskList taskList = adapter.getTaskAt(position);
            Intent intent = new Intent(v.getContext(), EditTaskActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(EditTaskActivity.EDIT_TASK, taskList.id);
            intent.putExtras(bundle);
            startActivityForResult(intent, EDIT_TASK_ACTIVITY_REQUEST_CODE);
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //put deleteAllTasks into menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret;
        switch (item.getItemId()) {
            case R.id.delete_all_tasks:
                mTaskViewModel.deleteAllTasks();
                Toast.makeText(getContext(), R.string.all_tasks_deleted, Toast.LENGTH_SHORT).show();
                ret = true;
                break;
            case R.id.show_created:
                if (item.isChecked()){
                    item.setChecked(false);
                    isCreatedChecked = false;
                }
                else {
                    item.setChecked(true);
                    isCreatedChecked = true;
                }
                ret = true;
                break;
            case R.id.show_in_progress:
                if (item.isChecked()){
                    item.setChecked(false);
                    isInProgressChecked = false;
                }
                else {
                    item.setChecked(true);
                    isInProgressChecked = true;
                }
                ret = true;
                break;
            case R.id.show_closed:
                if (item.isChecked()){
                    item.setChecked(false);
                    isClosedChecked = false;
                }
                else {
                    item.setChecked(true);
                    isClosedChecked = true;
                }
                ret = true;
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }
        mTaskViewModel.filterTaskList(isCreatedChecked, isInProgressChecked, isClosedChecked).observe(getViewLifecycleOwner(), adapter::submitList);
        return ret;
    }
}