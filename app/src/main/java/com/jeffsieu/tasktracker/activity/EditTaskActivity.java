package com.jeffsieu.tasktracker.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jeffsieu.tasktracker.R;
import com.jeffsieu.tasktracker.Task;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Jeff on 24/6/2016.
 */
public class EditTaskActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "EditTaskActivity";
    private Task task;
    private View mDueDateView;
    private View mSubjectView;

    private TextView mTitleText;
    private TextInputEditText mTitleEditText;
    private TextView mDueDateText;
    private TextView mSubjectText;

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        //toolbar.setBackgroundColor(Color.parseColor("#AAAAAA"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.task_title_layout);

        mTitleText = (TextView) findViewById(R.id.task_title_text);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean keyboardShown = true;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
                int fadeStartOffset = -100;
                int fadeEndOffset = -200;
                if (offset > fadeStartOffset) {
                    // not collapsed
                    textInputLayout.setAlpha(1);
                    keyboardShown = true;
                    mTitleText.setAlpha(0);
                    mTitleEditText.setCursorVisible(true);

                } else if (offset < fadeEndOffset){
                    // collapsed
                    textInputLayout.setAlpha(0);
                    textInputLayout.setVisibility(View.GONE);
                    if (keyboardShown) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textInputLayout.getWindowToken(), 0);
                        findViewById(R.id.main).requestFocus();
                        mTitleEditText.setCursorVisible(false);
                        keyboardShown = false;
                    }
                    mTitleText.setAlpha(1);
                } else {
					textInputLayout.setVisibility(View.VISIBLE);
                    textInputLayout.setAlpha((float)Math.abs(offset - fadeEndOffset)/(float)Math.abs(fadeEndOffset-fadeStartOffset));
                    mTitleText.setAlpha(((float)Math.abs(offset - fadeStartOffset)/(float)Math.abs(fadeEndOffset-fadeStartOffset)));
                    keyboardShown = true;
                }
            }
        });

        mDueDateView = findViewById(R.id.due_date);
        mDueDateView.setOnClickListener(this);

        mSubjectView = findViewById(R.id.subject);
        mSubjectView.setOnClickListener(this);

        mDueDateText = (TextView) findViewById(R.id.due_date_text);
        mSubjectText = (TextView) findViewById(R.id.subject_text);

        mTitleEditText = (TextInputEditText) findViewById(R.id.task_title_edit_text);
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                task.setName(charSequence.toString());
                mTitleText.setText(task.getName());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

		task = getIntent().getParcelableExtra("task");
		if (task != null) {
			mTitleText.setText(task.getName());
			mTitleEditText.setText(task.getName());
			mDueDateText.setText(task.getDateString());
		} else {
			task = new Task();
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_edit_task_bar, menu);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
			finish();
        } else if (id == R.id.action_save) {
            Intent intent = new Intent();
            intent.putExtra("task", task);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mDueDateView)) {
            Calendar calendar = task.getDate();
            DatePickerDialog dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else if (view.equals(mSubjectView)) {
            final String[] stuff = {"English", "Chinese"};
            new AlertDialog.Builder(this).setTitle("Choose subject").setItems(stuff, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mSubjectText.setText(stuff[i]);
                }
            }).show();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        task.setDate(new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
        mDueDateText.setText(task.getDateString());
    }
}
