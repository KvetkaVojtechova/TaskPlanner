package com.flowertech.taskplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class TaskListFragment extends Fragment {
    private TaskViewModel mTaskViewModel;
    private final TaskListAdapter adapter = new TaskListAdapter(new TaskListAdapter.TaskDiff());
    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_TASK_ACTIVITY_REQUEST_CODE = 2;
    public static final String SHARED_PREF_CREATED = "createdKey";
    public static final String SHARED_PREF_IN_PROGRESS = "inProgressKey";
    public static final String SHARED_PREF_CLOSED = "closedKey";
    private boolean isCreatedChecked;
    private boolean isInProgressChecked;
    private boolean isClosedChecked;

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

        //get settings from shared preferences
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        isCreatedChecked = pref.getBoolean(SHARED_PREF_CREATED, true);
        isInProgressChecked = pref.getBoolean(SHARED_PREF_IN_PROGRESS, true);
        isClosedChecked = pref.getBoolean(SHARED_PREF_CLOSED, false);

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

        Intent intt = getActivity().getIntent();
        if (intt != null){
            Bundle bund = intt.getExtras();
            if (bund != null) {
                Long id = bund.getLong(EditTaskActivity.EDIT_TASK);
                if (id != null) {
                    Intent intent = new Intent(v.getContext(), EditTaskActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(EditTaskActivity.EDIT_TASK, id);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, EDIT_TASK_ACTIVITY_REQUEST_CODE);
                }
            }
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuCompat.setGroupDividerEnabled(menu, true);

        MenuItem createdMenuItem = menu.findItem(R.id.show_created);
        createdMenuItem.setChecked(isCreatedChecked);
        MenuItem inProgressMenuItem = menu.findItem(R.id.show_in_progress);
        inProgressMenuItem.setChecked(isInProgressChecked);
        MenuItem closedMenuItem = menu.findItem(R.id.show_closed);
        closedMenuItem.setChecked(isClosedChecked);
    }

    //put deleteAllTasks into menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret;
        switch (item.getItemId()) {
            case R.id.delete_all_tasks:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.alert_delete_title);
                builder.setMessage(R.string.alert_delete_message);
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                    mTaskViewModel.deleteAllTasks();
                    Toast.makeText(getContext(), R.string.all_tasks_deleted, Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton(R.string.no, null);
                builder.show();
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
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(SHARED_PREF_CREATED, isCreatedChecked);
        edit.putBoolean(SHARED_PREF_IN_PROGRESS, isInProgressChecked);
        edit.putBoolean(SHARED_PREF_CLOSED, isClosedChecked);
        edit.apply();
        mTaskViewModel.filterTaskList(isCreatedChecked, isInProgressChecked, isClosedChecked).observe(getViewLifecycleOwner(), adapter::submitList);
        return ret;
    }
}