package com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.shawlabs.statussaver.Adapters.FilesGrabberAdapter;
import com.shawlabs.statussaver.R;
import com.shawlabs.statussaver.Utilities;
import com.shawlabs.statussaver.modal.satusModal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Images_Business extends Fragment {

    public static ArrayList<satusModal> list_of_files_business;
    public static satusModal stausModal;

    File targetDir;
    DocumentFile root;

    private MyAsyncTask_Images_Business mAsyncTask;
    RecyclerView mRecyclerView;
    FilesGrabberAdapter mDocumentFileAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    Button whatsAppSend;
    LinearLayout nothingFound_directMessageBox_img_business;

    public Images_Business() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAsyncTask == null || mAsyncTask.getStatus() != AsyncTask.Status.RUNNING)
        {
            mAsyncTask = new MyAsyncTask_Images_Business();
            mAsyncTask.execute();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_images, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerViewFromImagesFragment);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        nothingFound_directMessageBox_img_business = view.findViewById(R.id.nothingFound_directMessageBox_img);
        whatsAppSend = view.findViewById(R.id.whatsAppSend);


        mDocumentFileAdapter = new FilesGrabberAdapter(getContext());
        mRecyclerView.setAdapter(mDocumentFileAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (mAsyncTask == null || mAsyncTask.getStatus() != AsyncTask.Status.RUNNING)
                {

                    mAsyncTask = new MyAsyncTask_Images_Business();
                    mAsyncTask.execute();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if(list_of_files_business != null && !list_of_files_business.isEmpty()){


            mDocumentFileAdapter.setFiles(list_of_files_business);
            mDocumentFileAdapter.notifyDataSetChanged();
        }

        if(!Utilities.is_below_android_11)
        {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("ShakirStatusSaverSharedPref", MODE_PRIVATE);
            Uri treeUri = Uri.parse(sharedPreferences.getString("pathToTheWhatsAppBusinessFolder", null));

            root = DocumentFile.fromTreeUri(getContext(), treeUri);
        } else
        {

            targetDir = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "/WA Business/Media/.Statuses");

            if(!targetDir.isDirectory())
                targetDir = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "/Android/media/com.whatsapp.w4b/WA Business/Media/.Statuses");

        }

        return view;
    }

    private class MyAsyncTask_Images_Business extends AsyncTask<Void, List<satusModal>, Void> {

        int i = 0;
        int progress = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list_of_files_business = new ArrayList<>();
        }

        protected Void doInBackground(Void... params) {

            // Get the list of files in the root directory

            if(!Utilities.is_below_android_11){

            DocumentFile[] file = root.listFiles();
            for (; i < file.length; i++) {

                if (file[i].getName().endsWith(".jpg")) {

                    stausModal = new satusModal(file[i].getName(), file[i].getUri());
                    list_of_files_business.add(stausModal);
                }

                progress = (int) ((i / (float) file.length) * 100);

                if (progress % 5 == 0) {
                    publishProgress(list_of_files_business);
                }
            }

            }else
            {
                File[] file = targetDir.listFiles();

                if (file != null) {
                   // Arrays.sort(file, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

                    for ( ; i < file.length; i++) {

                        if (Uri.fromFile(file[i]).toString().endsWith(".jpg") )
                        {
                            stausModal = new satusModal(file[i].getName(),Uri.fromFile(file[i])) ;
                            list_of_files_business.add(stausModal);
                        }

                        progress = (int) ((i / (float) file.length) * 100);

                        if (progress % 5 == 0) {
                            publishProgress(list_of_files_business);
                        }
                    }

                }
            }


            return null;
        }

        protected void onProgressUpdate(List<satusModal>... progress) {
            mDocumentFileAdapter.setFiles(progress[0]);
            mDocumentFileAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if(list_of_files_business.size()<1) {
                nothingFound_directMessageBox_img_business.setVisibility(View.VISIBLE);

                whatsAppSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openWhatsApp_WABusiness();
                    }
                });
            }
        else
                nothingFound_directMessageBox_img_business.setVisibility(View.GONE);

        }
    }

    public void openWhatsApp_WABusiness(){

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setComponent(new ComponentName("com.whatsapp.w4b", "com.whatsapp.Main"));
        startActivity(i);
    }

    }
