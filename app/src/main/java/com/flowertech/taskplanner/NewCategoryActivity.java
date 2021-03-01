package com.flowertech.taskplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

public class NewCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY =
            "com.flowertech.categorylistsql.EXTRA_CATEGORY";

    private EditText mEditTextAbbr;
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        mEditTextAbbr = findViewById(R.id.edit_text_abbr);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        final Button button = findViewById(R.id.button_save);

        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();

            if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                    TextUtils.isEmpty(mEditTextDescription.getText()) &&
                    TextUtils.isEmpty(mEditTextAbbr.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                Category category = new Category();
                category.abbr = mEditTextAbbr.getText().toString();
                category.title = mEditTextTitle.getText().toString();
                category.description = mEditTextDescription.getText().toString();

                if (category.abbr.length() == 0){
                    setResult(RESULT_CANCELED, replyIntent);
                    Toast.makeText(getApplicationContext(),
                            R.string.new_category_no_abbr,
                            Toast.LENGTH_LONG).show();
                    return;
                } else if(category.title.length() == 0){
                    setResult(RESULT_CANCELED, replyIntent);
                    Toast.makeText(getApplicationContext(),
                            R.string.new_category_no_title,
                            Toast.LENGTH_LONG).show();
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable(EXTRA_CATEGORY, category);
                replyIntent.putExtras(bundle);

                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}