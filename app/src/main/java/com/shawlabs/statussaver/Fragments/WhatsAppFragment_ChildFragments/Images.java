package com.shawlabs.statussaver.Fragments.WhatsAppFragment_ChildFragments;

import static android.content.Context.MODE_PRIVATE;


import static com.shawlabs.statussaver.Utilities.is_below_android_11;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.shawlabs.statussaver.Adapters.FilesGrabberAdapter;
import com.shawlabs.statussaver.R;
import com.shawlabs.statussaver.modal.satusModal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Images extends Fragment {


    public static List<satusModal> list_of_files ;
    public static satusModal stausModal;

    File targetDir;
    DocumentFile root;

    Button whatsAppSend;
    private MyAsyncTask_Images mAsyncTask;
    RecyclerView mRecyclerView;
    FilesGrabberAdapter mDocumentFileAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    View view;
    LinearLayout nothingFound_directMessageBox_img;

    public Images() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_images, container, false);




        mRecyclerView = view.findViewById(R.id.recyclerViewFromImagesFragment);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        nothingFound_directMessageBox_img = view.findViewById(R.id.nothingFound_directMessageBox_img);
        whatsAppSend = view.findViewById(R.id.whatsAppSend);


        mDocumentFileAdapter = new FilesGrabberAdapter(getContext());
        mRecyclerView.setAdapter(mDocumentFileAdapter);

        if(list_of_files != null && !list_of_files.isEmpty()){

            mDocumentFileAdapter.setFiles(list_of_files);
            mDocumentFileAdapter.notifyDataSetChanged();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (mAsyncTask == null || mAsyncTask.getStatus() != AsyncTask.Status.RUNNING)
                {

                    mAsyncTask = new MyAsyncTask_Images();
                    mAsyncTask.execute();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if(!is_below_android_11)
        {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("ShakirStatusSaverSharedPref", MODE_PRIVATE);
            Uri treeUri = Uri.parse(sharedPreferences.getString("pathToTheWhatsAppFolder", null));

             root = DocumentFile.fromTreeUri(getContext(), treeUri);
        } else
        {

             targetDir = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "WhatsApp/Media/.Statuses/");

            if(!targetDir.exists()) {
                targetDir = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses");
                Toast.makeText(getContext(), "222", Toast.LENGTH_SHORT).show();
            } else                 Toast.makeText(getContext(), "111 ", Toast.LENGTH_SHORT).show();


            File[] file = targetDir.listFiles();
            if(file !=null)
                Toast.makeText(getContext(), " "+file.length, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), " NULL", Toast.LENGTH_SHORT).show();

        }

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();


        if (mAsyncTask == null || mAsyncTask.getStatus() != AsyncTask.Status.RUNNING)
        {

            mAsyncTask = new MyAsyncTask_Images();
            mAsyncTask.execute();

        }
    }


    private class MyAsyncTask_Images extends AsyncTask<Void, List<satusModal>, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            list_of_files= new ArrayList<>();
        }

        int i = 0;
        int progress = 0;
        protected Void doInBackground(Void... params) {


            if(!is_below_android_11) {

                DocumentFile[] file = root.listFiles();
                //Arrays.sort(file, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

                for (; i < file.length; i++) {

                    if (file[i].getName().endsWith(".jpg")) {

                        stausModal = new satusModal(file[i].getName(),file[i].getUri()) ;
                        list_of_files.add(stausModal);
                    }

                    progress = (int) ((i / (float) file.length) * 100);

                    if (progress % 5 == 0) {
                     publishProgress(list_of_files);
                    }

                }
            }
            else{

                File[] file = targetDir.listFiles();

                if (file != null) {
                    //Arrays.sort(file, LastModifiedFileComparator.LASTMODIFIED_REVERSE);


                    for ( ; i < file.length; i++) {

                        if (Uri.fromFile(file[i]).toString().endsWith(".jpg") )
                        {
                            stausModal = new satusModal(file[i].getName(),Uri.fromFile(file[i])) ;
                            list_of_files.add(stausModal);
                        }

                        progress = (int) ((i / (float) file.length) * 100);

                        if (progress % 5 == 0) {
                            publishProgress(list_of_files);
                        }
                    }

                }

            }

            // publishProgress(i);

            return null;
        }

        protected void onProgressUpdate(List<satusModal>... progress) {

            mDocumentFileAdapter.setFiles(progress[0]);
            mDocumentFileAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if(list_of_files.size()<1) {
                nothingFound_directMessageBox_img.setVisibility(View.VISIBLE);


                whatsAppSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openWhatsApp();
                    }
                });
            }
            else
                nothingFound_directMessageBox_img.setVisibility(View.GONE);

        }
    }//END ASYNC


    public void openWhatsApp(){

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Main"));

        startActivity(intent);

    }
}

