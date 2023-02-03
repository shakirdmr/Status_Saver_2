package com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.shawlabs.statussaver.Adapters.SavedStatusAdapter;
import com.shawlabs.statussaver.R;
import com.shawlabs.statussaver.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Saved extends Fragment {

    ArrayList<File> list = new ArrayList<>();
    RecyclerView mRecyclerView;
    SavedStatusAdapter adapter;

    LinearLayout nothingFound_directMessageBox_saved;
    SwipeRefreshLayout swipeRefreshLayout;


    public Saved() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        getDataFromFolder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerViewFromImagesFragment);
        nothingFound_directMessageBox_saved = view.findViewById(R.id.nothingFound_directMessageBox_saved);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_saved);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getDataFromFolder();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void getDataFromFolder() {


        File targetDir = new File(Utilities.downloadFolder);
        File[] allFiles = targetDir.listFiles();

        if(!(allFiles ==null)) {

            list.clear();
           // Arrays.sort(allFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            list.addAll(Arrays.asList(allFiles));
        }


        if(list.size()<1)
            nothingFound_directMessageBox_saved.setVisibility(View.VISIBLE);
       else {
            adapter = new SavedStatusAdapter(list, getContext());
            mRecyclerView.setAdapter(adapter);
        }
    }
}