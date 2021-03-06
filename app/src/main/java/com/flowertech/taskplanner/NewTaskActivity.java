package com.flowertech.taskplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class NewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static final String EXTRA_TASK =
            "com.flowertech.tasklistsql.EXTRA_TASK";

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private Spinner mSpinnerCategory;
    private NewTaskViewModel mNewTaskViewModel;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        mNewTaskViewModel = new ViewModelProvider(this).get(NewTaskViewModel.class);

        task = new Task();

        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        mSpinnerCategory = findViewById(R.id.spinner_category);

        //menu back
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24);
        setTitle("New Task");

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v ->
                new DateTimePicker().invoke(
                        NewTaskActivity.this,
                        (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) ->
                                mTextViewDueDate.setText(selectedDay + "." + selectedMonth + "." + selectedYear + " " + selectedHour + ":" + selectedMinute)
                )
        );

        //spinner category
        mSpinnerCategory.setOnItemSelectedListener(this);

        CategorySpinner categorySpinner = new CategorySpinner();
        categorySpinner.createSpinner(mNewTaskViewModel, this, task, mSpinnerCategory, this);
    }

    private void addTask() {
        //validate inputs
        if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                TextUtils.isEmpty(mEditTextDescription.getText()) &&
                TextUtils.isEmpty(mTextViewDueDate.getText())) {
            Toast.makeText(
                    this,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        } else {
            task.title = mEditTextTitle.getText().toString();
            task.description = mEditTextDescription.getText().toString();
            task.state = State.created;

            if (task.title.length() == 0){
                Toast.makeText(getApplicationContext(),
                        R.string.new_task_no_title,
                        Toast.LENGTH_LONG).show();
                return;
            }

            Date dueDate = DateConverters.StringToDate(mTextViewDueDate.getText().toString());
            task.dueDate = dueDate;

            mNewTaskViewModel.insert(task);
        }
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getSelectedItem();
        if (position == 0){
            task.categoryId = null;
        } else {
            task.categoryId = category.id;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task:
                addTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}