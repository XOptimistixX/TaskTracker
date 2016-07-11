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

    public ChannelOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChannelOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChannelOverviewFragment newInstance() {
        ChannelOverviewFragment fragment = new ChannelOverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_overview, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_channel_list);
        recyclerView.setAdapter(new ChannelOverviewAdapter(getContext(), MainActivity.channels));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        return view;

    }
}
