package com.flowertech.taskplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditCategoryActivity extends AppCompatActivity {

    public static final String EDIT_CATEGORY =
            "com.flowertech.categorylistsql.EDIT_CATEGORY";

    private EditText mEditTextAbbr;
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Category category = (Category) bundle.getSerializable(EDIT_CATEGORY);

        mEditTextAbbr = findViewById(R.id.edit_text_abbr);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        final Button button = findViewById(R.id.button_save);

        //fill out the fields with existing data
        mEditTextAbbr.setText(category.abbr);
        mEditTextTitle.setText(category.title);
        mEditTextDescription.setText(category.description);

        button.setOnClickListener(view -> {

            if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                    TextUtils.isEmpty(mEditTextDescription.getText()) &&
                    TextUtils.isEmpty(mEditTextAbbr.getText())) {
                setResult(RESULT_CANCELED, intent);
            } else {
                category.abbr = mEditTextAbbr.getText().toString();
                category.title = mEditTextTitle.getText().toString();
                category.description = mEditTextDescription.getText().toString();

                if (category.abbr.length() == 0){
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(getApplicationContext(),
                            R.string.new_category_no_abbr,
                            Toast.LENGTH_LONG).show();
                    return;
                } else if(category.title.length() == 0){
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(getApplicationContext(),
                            R.string.new_category_no_title,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                bundle.putSerializable(EDIT_CATEGORY, category);
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
            }
            finish();
        });
    }
}