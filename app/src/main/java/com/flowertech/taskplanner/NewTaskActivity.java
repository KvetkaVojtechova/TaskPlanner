package com.flowertech.taskplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class NewTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK =
            "com.flowertech.tasklistsql.EXTRA_TASK";

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        final Button button = findViewById(R.id.button_save);

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v ->
                new DateTimePicker().invoke(
                        NewTaskActivity.this,
                        (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) ->
                                mTextViewDueDate.setText(selectedDay + "." + selectedMonth + "." + selectedYear + " " + selectedHour + ":" + selectedMinute)
                )
        );

        //when button is clicked validate data and setResult
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();

            if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                    TextUtils.isEmpty(mEditTextDescription.getText()) &&
                    TextUtils.isEmpty(mTextViewDueDate.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                Task task = new Task();
                task.title = mEditTextTitle.getText().toString();
                task.description = mEditTextDescription.getText().toString();
                task.state = State.created;

                if (task.title.length() == 0){
                    setResult(RESULT_CANCELED, replyIntent);
                    Toast.makeText(getApplicationContext(),
                            R.string.new_task_no_title,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Date dueDate = DateConverters.StringToDate(mTextViewDueDate.getText().toString());
                /*if (dueDate == null){
                    setResult(RESULT_CANCELED, replyIntent);
                    Toast.makeText(getApplicationContext(),
                            R.string.new_task_wrong_date,
                            Toast.LENGTH_LONG).show();
                    return;
                }*/

                task.dueDate = dueDate;

                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_TASK, task);
                replyIntent.putExtras(bundle);

                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}