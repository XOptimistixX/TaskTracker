package com.jeffsieu.tasktracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeffsieu.tasktracker.activity.MainActivity;

public class ChannelOverviewFragment extends Fragment {
	private RecyclerView recyclerView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_channel_overview, container, false);
		recyclerView = (RecyclerView) view.findViewById(R.id.fragment_channel_list);
		recyclerView.setAdapter(new ChannelOverviewAdapter(getContext(), MainActivity.channels));
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
		return view;
	}

	public void updateChannels() {
		recyclerView.getAdapter().notifyDataSetChanged();
	}

	public void notifyItemChanged(int position) {
		recyclerView.getAdapter().notifyItemChanged(position);
	}
}
