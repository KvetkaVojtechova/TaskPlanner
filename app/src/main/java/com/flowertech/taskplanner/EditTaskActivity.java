package com.flowertech.taskplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

public class EditTaskActivity extends AppCompatActivity {

    public static final String EDIT_TASK =
            "com.flowertech.tasklistsql.EDIT_TASK";

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private ImageView mImageStateCreated;
    private ImageView mImageStateInProgress;
    private ImageView mImageStateClosed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Task task = (Task) bundle.getSerializable(EDIT_TASK);

        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        mImageStateCreated = findViewById(R.id.image_view_state_1);
        mImageStateInProgress = findViewById(R.id.image_view_state_2);
        mImageStateClosed = findViewById(R.id.image_view_state_3);
        final Button button = findViewById(R.id.button_save);

        //fill out the fields with existing data
        mEditTextTitle.setText(task.title);
        mEditTextDescription.setText(task.description);
        mTextViewDueDate.setText(DateConverters.DateToString(task.dueDate));

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v ->
                new DateTimePicker().invoke(
                        EditTaskActivity.this,
                        (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) ->
                                mTextViewDueDate.setText(selectedDay + "." + selectedMonth + "." + selectedYear + " " + selectedHour + ":" + selectedMinute)
                )
        );

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
            //Intent replyIntent = new Intent();

            if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                    TextUtils.isEmpty(mEditTextDescription.getText()) &&
                    TextUtils.isEmpty(mTextViewDueDate.getText())) {
                setResult(RESULT_CANCELED, intent);
            } else {
                //Task task = new Task();
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
}