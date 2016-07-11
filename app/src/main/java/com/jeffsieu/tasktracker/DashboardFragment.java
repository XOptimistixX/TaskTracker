package com.jeffsieu.tasktracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeffsieu.tasktracker.activity.MainActivity;

/**
 * Created by Jeff on 24/6/2016.
 */
public class DashboardFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_dashboard_list);
        recyclerView.setAdapter(new DashboardAdapter(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        return view;
    }

    public void notifyItemChanged(int position) {
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    public void updateDashboard() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

}
