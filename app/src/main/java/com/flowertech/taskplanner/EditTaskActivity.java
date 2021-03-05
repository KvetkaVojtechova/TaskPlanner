package com.flowertech.taskplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.util.List;
import java.util.Observer;

public class EditTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static final String EDIT_TASK =
            "com.flowertech.tasklistsql.EDIT_TASK";

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private ImageView mImageStateCreated;
    private ImageView mImageStateInProgress;
    private ImageView mImageStateClosed;
    private Spinner mSpinnerCategory;
    private EditTaskViewModel mEditTaskViewModel;

    private Task task;
    private Intent intent;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        mEditTaskViewModel = new ViewModelProvider(this).get(EditTaskViewModel.class);

        intent = getIntent();
        bundle = intent.getExtras();
        task = (Task) bundle.getSerializable(EDIT_TASK);

        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        mImageStateCreated = findViewById(R.id.image_view_state_1);
        mImageStateInProgress = findViewById(R.id.image_view_state_2);
        mImageStateClosed = findViewById(R.id.image_view_state_3);
        mSpinnerCategory = findViewById(R.id.spinner_category);
        final Button button = findViewById(R.id.button_save);

        //fill out the fields with existing data
        mEditTextTitle.setText(task.title);
        mEditTextDescription.setText(task.description);
        if(task.dueDate != null)
            mTextViewDueDate.setText(DateConverters.DateToString(task.dueDate));

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v ->
                new DateTimePicker().invoke(
                        EditTaskActivity.this,
                        (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) ->
                                mTextViewDueDate.setText(selectedDay + "." + selectedMonth + "." + selectedYear + " " + selectedHour + ":" + selectedMinute)
                )
        );

        mSpinnerCategory.setOnItemSelectedListener(this);

        mEditTaskViewModel.getAllCategories().observe(this, categoryEntities -> {
                // Creating adapter for spinner
                ArrayAdapter<Category> categoryAdapter =
                        new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoryEntities);
                //find selected category
                Category selectedCategory = categoryEntities.stream()
                        .filter(category -> category.id == task.categoryId).findFirst().orElse(null);
                // Drop down layout style - list view with radio button
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                mSpinnerCategory.setAdapter(categoryAdapter);
                //preselect category in spinner
                if (selectedCategory != null)
                    mSpinnerCategory.setSelection(categoryAdapter.getPosition(selectedCategory));
        });

        mImageStateCreated.setOnClickListener(v -> {
            mImageStateCreated.setBackgroundColor(Color.rgb(195, 236, 241));
            mImageStateInProgress.setBackgroundColor(Color.rgb(255, 255, 255));
            mImageStateClosed.setBackgroundColor(Color.rgb(255, 255, 255));
            task.state = State.created;
        });

        mImageStateInProgress.setOnClickListener(v -> {
            mImageStateInProgress.setBackgroundColor(Color.rgb(195, 236, 241));
            mImageStateCreated.setBackgroundColor(Color.rgb(255, 255, 255));
            mImageStateClosed.setBackgroundColor(Color.rgb(255, 255, 255));
            task.state = State.inProgress;
        });

        mImageStateClosed.setOnClickListener(v -> {
            mImageStateClosed.setBackgroundColor(Color.rgb(195, 236, 241));
            mImageStateCreated.setBackgroundColor(Color.rgb(255, 255, 255));
            mImageStateInProgress.setBackgroundColor(Color.rgb(255, 255, 255));
            task.state = State.closed;
        });

        //when button is clicked validate data and setResult
        button.setOnClickListener(view -> {
            if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                    TextUtils.isEmpty(mEditTextDescription.getText()) &&
                    TextUtils.isEmpty(mTextViewDueDate.getText())) {
                setResult(RESULT_CANCELED, intent);
            } else {
                task.title = mEditTextTitle.getText().toString();
                task.description = mEditTextDescription.getText().toString();

                if (task.title.length() == 0){
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(getApplicationContext(),
                            R.string.new_task_no_title,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Date dueDate = DateConverters.StringToDate(mTextViewDueDate.getText().toString());
                task.dueDate = dueDate;

                //Bundle bundle = new Bundle();
                bundle.putSerializable(EDIT_TASK, task);
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
            }
            finish();
        });
    }

    private void saveTask() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getSelectedItem();
        task.categoryId = category.id;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_task_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
                saveTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
