package com.flowertech.taskplanner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class NewCategoryActivity extends AppCompatActivity {

    private EditText mEditTextAbbr;
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private NewCategoryViewModel mNewCategoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        mNewCategoryViewModel = new ViewModelProvider(this).get(NewCategoryViewModel.class);

        mEditTextAbbr = findViewById(R.id.edit_text_abbr);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);

        //menu back
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24);
        setTitle(R.string.activity_new_category_title);
    }

    private void addCategory(){
        if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                TextUtils.isEmpty(mEditTextDescription.getText()) &&
                TextUtils.isEmpty(mEditTextAbbr.getText())) {
            Toast.makeText(
                    this,
                    R.string.empty_not_saved_cat,
                    Toast.LENGTH_LONG).show();
        } else {
            Category category = new Category();
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

            mNewCategoryViewModel.insert(category);
        }
        finish();
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
                addCategory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}