package com.jeffsieu.tasktracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.jeffsieu.tasktracker.activity.MainActivity;

import java.util.List;

/**
 * Created by user on 10/7/2016.
 */
public class ChannelOverviewAdapter extends RecyclerView.Adapter<ChannelOverviewAdapter.ChannelViewholder> {
    private Context context;
    private List<Channel> channels;
    private RecyclerView mRecyclerView;

    private int lastPosition = -1;
    private long lastAnimationTime = -1;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public ChannelOverviewAdapter(Context context, List<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }

    @Override
    public ChannelViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        return new ChannelViewholder(view);
    }

    @Override
    public void onBindViewHolder(final ChannelViewholder holder, final int position) {
        final Channel channel = MainActivity.channels.get(position);
        final String channelKey = MainActivity.channelKeys.get(position);
        holder.mNameView.setAlpha(1);
        holder.mCodeView.setAlpha(1);
        /*
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("task", task);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                        Pair.create(holder.mRootView, "card"),
                        Pair.create((View) holder.mNameView, "name"),
                        Pair.create((View) holder.mTimeView, "time"),
                        Pair.create(holder.mScheduleView, "schedule"));
                Bundle bundle = options.toBundle();
                context.startActivity(intent, bundle);
            }
        });
        */

        holder.mNameView.setText(channel.getName());
        holder.mCodeView.setText(channel.getCode());

        setAnimation(holder.mRootView, position);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            if (System.currentTimeMillis() - lastAnimationTime < 2 * 50 || lastAnimationTime == -1) {
                viewToAnimate.startAnimation(animation);
                lastAnimationTime = System.currentTimeMillis();
            }
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public class ChannelViewholder extends RecyclerView.ViewHolder {
        protected View mRootView;
        protected TextView mNameView;
        protected TextView mCodeView;

        public ChannelViewholder(View v) {
            super(v);
            mRootView = v.findViewById(R.id.item_channel_root);
            mNameView = (TextView) v.findViewById(R.id.item_channel_name);
            mCodeView = (TextView) v.findViewById(R.id.item_channel_code);
        }
    }
}
