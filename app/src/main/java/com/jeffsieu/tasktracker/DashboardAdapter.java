package com.jeffsieu.tasktracker;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.jeffsieu.tasktracker.activity.MainActivity;
import com.jeffsieu.tasktracker.activity.TaskActivity;
import com.jeffsieu.tasktracker.utils.DatabaseUtils;

import java.util.List;

/**
 * Created by Jeff on 24/6/2016.
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewholder> {
    private Context context;
	private RecyclerView mRecyclerView;
	private List<Task> tasks = MainActivity.tasks;
	private boolean isTickEnabled = true;

	private int lastPosition = -1;
	private long lastAnimationTime = -1;

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		mRecyclerView = recyclerView;
	}

	public DashboardAdapter(Context context) {
        this.context = context;
    }

    @Override
    public DashboardViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new DashboardViewholder(view);
    }

    @Override
    public void onBindViewHolder(final DashboardViewholder holder, final int position) {
        final Task task = tasks.get(position);
		final String taskKey = MainActivity.taskKeys.get(position);
		holder.mTickView.setScaleX(1);
		holder.mTickView.setScaleY(1);
		holder.mScheduleView.setAlpha(1);
		holder.mNameView.setAlpha(1);
		holder.mTimeView.setAlpha(1);
		holder.mBigTickView.setScaleX(0);
		holder.mBigTickView.setScaleY(0);
		holder.mBackgroundView.setVisibility(View.GONE);
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("task", task);
				intent.putExtra("taskKey", taskKey);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                        Pair.create(holder.mRootView, "card"),
                        Pair.create((View)holder.mNameView, "name"),
                        Pair.create((View)holder.mTimeView, "time"),
                        Pair.create(holder.mScheduleView, "schedule"));
                Bundle bundle = options.toBundle();
                context.startActivity(intent, bundle);
            }
        });
        holder.mTickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				if (isTickEnabled) {
					isTickEnabled = false;
					int time = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
					int shortTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
					holder.mTickView.animate().scaleX(0).scaleY(0).setDuration(shortTime).start();
					holder.mScheduleView.animate().alpha(0).setDuration(shortTime).start();
					holder.mNameView.animate().alpha(0).setDuration(shortTime).start();
					holder.mTimeView.animate().alpha(0).setDuration(shortTime).start();
					holder.mBigTickView.setScaleX(0);
					holder.mBigTickView.setScaleY(0);
					holder.mBigTickView.animate().scaleX(1).scaleY(1).setDuration(time).setStartDelay(time).setInterpolator(new OvershootInterpolator()).setListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animator) {

						}

						@Override
						public void onAnimationEnd(Animator animator) {
							MainActivity.tasks.remove(task);
							MainActivity.taskKeys.remove(taskKey);
							notifyItemRemoved(position);
							notifyItemRangeChanged(position, tasks.size());
							Snackbar snackbar = Snackbar.make(holder.mRootView, "Task completed", Snackbar.LENGTH_LONG);
							snackbar.setAction("Undo", new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									MainActivity.tasks.add(position, task);
									MainActivity.taskKeys.add(position, taskKey);
									notifyItemInserted(position);
									notifyItemRangeChanged(position, tasks.size());
									holder.mTickView.setScaleX(1);
									holder.mTickView.setScaleY(1);
									holder.mScheduleView.setAlpha(1);
									holder.mNameView.setAlpha(1);
									holder.mTimeView.setAlpha(1);
									holder.mBigTickView.setScaleX(0);
									holder.mBigTickView.setScaleY(0);
									holder.mBackgroundView.setVisibility(View.GONE);
								}
							}).show();
							if (!(((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() < mRecyclerView.getAdapter().getItemCount() - 1)) {
								//cannot scroll anymore
								MainActivity.sReference.showFab();
							}
							isTickEnabled = true;
							snackbar.setCallback(new Snackbar.Callback() {
								@Override
								public void onDismissed(Snackbar snackbar, int event) {
									//actually delete it from storage
									if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
										System.out.println("Removing " + task.getName());
										DatabaseUtils.removeTask(taskKey);
									}
								}
							});
						}

						@Override
						public void onAnimationCancel(Animator animator) {

						}

						@Override
						public void onAnimationRepeat(Animator animator) {

						}
					}).start();
					holder.mBackgroundView.setVisibility(View.VISIBLE);
					if (Build.VERSION.SDK_INT >= 21) {
						ViewAnimationUtils.createCircularReveal(holder.mBackgroundView, holder.mRootView.getWidth() - MainActivity.dpToPx(28), holder.mRootView.getHeight() - MainActivity.dpToPx(28), 0, holder.mRootView.getWidth()).setDuration(time).start();
					} else {
						holder.mBackgroundView.setAlpha(0);
						holder.mBackgroundView.animate().alpha(1).setDuration(time).start();
					}
				}
            }
        });

		holder.mNameView.setText(task.getName());
		holder.mTimeView.setText(task.getDateString() + " - " + task.getTimeLeft());
		holder.mSubjectView.setText("");

		setAnimation(holder.mRootView, position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

	private void setAnimation(View viewToAnimate, int position)
	{
		// If the bound view wasn't previously displayed on screen, it's animated
		if (position > lastPosition)
		{
			Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
			if (System.currentTimeMillis() - lastAnimationTime < 2 * 50 || lastAnimationTime == -1)
			{
				viewToAnimate.startAnimation(animation);
				lastAnimationTime = System.currentTimeMillis();
			}
			viewToAnimate.startAnimation(animation);
			lastPosition = position;
		}
	}

    public class DashboardViewholder extends RecyclerView.ViewHolder {
        protected View mRootView;
        protected TextView mNameView;
        protected TextView mTimeView;
        protected TextView mSubjectView;
        protected View mScheduleView;
        protected View mTickView;
		protected View mBigTickView;
        protected View mBackgroundView;

        public DashboardViewholder(View v) {
            super(v);
            mRootView = v.findViewById(R.id.item_task_root);
            mNameView = (TextView) v.findViewById(R.id.item_task_name);
            mTimeView = (TextView) v.findViewById(R.id.item_task_time);
            mSubjectView = (TextView) v.findViewById(R.id.item_task_subject);
            mScheduleView = v.findViewById(R.id.item_task_schedule);
            mTickView = v.findViewById(R.id.item_task_tick);
			mBigTickView = v.findViewById(R.id.item_task_tick_big);
			mBigTickView.setScaleX(0);
			mBigTickView.setScaleY(0);
            mBackgroundView = v.findViewById(R.id.item_task_background);
        }
    }
}
