package com.flowertech.taskplanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class EditCategoryActivity extends AppCompatActivity {

    public static final String EDIT_CATEGORY =
            "com.flowertech.categorylistsql.EDIT_CATEGORY";

    private TextView mTextViewCreated;
    private EditText mEditTextAbbr;
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private EditCategoryViewModel mEitCategoryViewModel;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        mEitCategoryViewModel = new ViewModelProvider(this).get(EditCategoryViewModel.class);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        category = (Category) bundle.getSerializable(EDIT_CATEGORY);

        String created = getResources().getString(R.string.date_created);

        mTextViewCreated = findViewById(R.id.text_view_created);
        mEditTextAbbr = findViewById(R.id.edit_text_abbr);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);

        //menu back
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24);
        setTitle(R.string.activity_edit_category_title);

        //fill out the fields with existing data
        mTextViewCreated.setText(created + DateConverters.DateToString(category.created));
        mEditTextAbbr.setText(category.abbr);
        mEditTextTitle.setText(category.title);
        mEditTextDescription.setText(category.description);
    }

    private void editCategory() {
        if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                TextUtils.isEmpty(mEditTextDescription.getText()) &&
                TextUtils.isEmpty(mEditTextAbbr.getText())) {
            Toast.makeText(
                    this,
                    R.string.empty_not_saved_cat,
                    Toast.LENGTH_LONG).show();
        } else {
            category.abbr = mEditTextAbbr.getText().toString();
            category.title = mEditTextTitle.getText().toString();
            category.description = mEditTextDescription.getText().toString();

            if (category.abbr.length() == 0){
                Toast.makeText(this,
                        R.string.new_category_no_abbr,
                        Toast.LENGTH_LONG).show();
                return;
            } else if(category.title.length() == 0){
                Toast.makeText(this,
                        R.string.new_category_no_title,
                        Toast.LENGTH_LONG).show();
                return;
            }

            mEitCategoryViewModel.update(category);
        }
        finish();
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
                editCategory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}