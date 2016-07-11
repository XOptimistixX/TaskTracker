package com.jeffsieu.tasktracker.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeffsieu.tasktracker.R;
import com.jeffsieu.tasktracker.Task;
import com.jeffsieu.tasktracker.utils.DatabaseUtils;

/**
 * Created by Jeff on 26/6/2016.
 */
public class TaskActivity extends AppCompatActivity implements View.OnClickListener {
    private Task task;
	private String taskKey;

    private TextView mNameView;
    private TextView mTimeView;
    private TextView mSubjectView;
    private ImageButton mTickView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        task = getIntent().getParcelableExtra("task");
		taskKey = getIntent().getStringExtra("taskKey");

        mNameView = (TextView) findViewById(R.id.item_task_name);
        mTimeView = (TextView) findViewById(R.id.item_task_time);
        mSubjectView = (TextView) findViewById(R.id.item_task_subject);
        mTickView = (ImageButton) findViewById(R.id.item_task_tick);

        mNameView.setText(task.getName());
		mTimeView.setText(task.getDateString() + " - " + task.getTimeLeft());
        mSubjectView.setText("");

        mTickView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.equals(mTickView)) {
			onBackPressed();
        }
    }

	@Override
	public void onBackPressed() {
		if (Build.VERSION.SDK_INT >= 21) {
			finishAfterTransition();
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
		} else if (id == R.id.action_edit) {
			Intent intent = new Intent(this, EditTaskActivity.class);
			Task task = new Task();
			task.importFromTask(this.task);
			intent.putExtra("task", task);
			startActivityForResult(intent, MainActivity.EDIT_TASK);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MainActivity.EDIT_TASK) {
			if (resultCode == RESULT_OK) {
				Task task = data.getParcelableExtra("task");
				DatabaseUtils.updateTask(task, taskKey);
				finish();
			}
		}
	}
}
