package com.shawlabs.statussaver.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Build.VERSION.SDK_INT;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shawlabs.statussaver.Adapters.WhatsAppFragmentAdapter;
import com.shawlabs.statussaver.R;


public class WhatsAppFragment extends Fragment {


    ViewPager viewPager ;
    TabLayout tabLayout  ;
    Button permissionsToReadFolder ;
    LinearLayout permissionsToReadFolder_box;

    private static final String KEY_STATE = "state";
    private int mState;


    public WhatsAppFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
View view =  inflater.inflate(R.layout.fragment_whats_app, container, false);




         viewPager = view.findViewById(R.id.view_pager);
         tabLayout =  view.findViewById(R.id.TabLayoutWhatsApp);
         permissionsToReadFolder =  view.findViewById(R.id.permissionsToReadFolder);
        permissionsToReadFolder_box =  view.findViewById(R.id.permissionsToReadFolder_box);





        //FOR ANDRIOD 11 and gerater
        if (SDK_INT >= Build.VERSION_CODES.R) {

                if(isPermissionToPeepInsideWhatsAppFolderGiven())
                {
                   preset_the_ADAPTER();
                   // collectTheDataFromWhatsAppFolderAndKeepItReadyToBeShownOnChildFragments();
                }
                else
                {
                    /*CREATE AN XML LAYOUT TO SHOW USER TO ALLOW PERMISSIOS THEN OPEN BELOW
                    FUNCTION ON PRESS OF BUTTON*/
                    permissionsToReadFolder_box.setVisibility(View.VISIBLE);

                    permissionsToReadFolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //OPEN FOLDER TREE
                            AskPermissionToPeepInsideWhatsAppFolder();
                        }
                    });

                }
        }
        else
            preset_the_ADAPTER();

        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return  view;
    } //END OF ON-CREATE


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.stateOfApp){
            openWhatsApp();
        }
        return  true;
    }

    private void preset_the_ADAPTER() {


        WhatsAppFragmentAdapter whatsAppFragmentAdapter = new WhatsAppFragmentAdapter(1,getContext(),getChildFragmentManager());
        viewPager.setAdapter(whatsAppFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private boolean isPermissionToPeepInsideWhatsAppFolderGiven() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("ShakirStatusSaverSharedPref", MODE_PRIVATE);

        //CHECK IF SHARED PREF HAS PATH
        //THEN CHECK IF I STILL HAVE PERMISSION TO READ FOLDER
        String path = sharedPreferences.getString("pathToTheWhatsAppFolder",null);
        Boolean statusForFolderRead = false;
        if(path!=null) {

            Uri folderUri = Uri.parse(path);
            DocumentFile folder = DocumentFile.fromTreeUri(getContext(), folderUri);

            if (folder != null) {
                if (folder.canRead())
                    statusForFolderRead = true;                // You have permission to read the contents of the folder
            }
        }

        return statusForFolderRead && sharedPreferences.getBoolean("isPermissionToWhatsAppFolderGiven", false);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("ShakirStatusSaverSharedPref", MODE_PRIVATE);

        if (requestCode == 10001 && resultCode == RESULT_OK) {

            if (data != null) {
                Uri treeUri = data.getData();

                String path = treeUri.getPath();

                if (path.endsWith(".Statuses")) {
                    permissionsToReadFolder_box.setVisibility(View.GONE);


                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    ContentResolver contentResolver = getContext().getContentResolver();
                    contentResolver.takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    editor.putBoolean("isPermissionToWhatsAppFolderGiven", true);
                    editor.putString("pathToTheWhatsAppFolder",  String.valueOf(treeUri));

                    editor.apply();

                    permissionsToReadFolder_box.setVisibility(View.GONE);
                    preset_the_ADAPTER();
                    // The user has granted your app access to the specific folder inside the WhatsApp folder
                } else {
                    Toast.makeText(getContext(), "Please give permission to correct folder \"FOLDER =  .Statuses \" ", Toast.LENGTH_LONG).show();

                    // The user has not granted your app access to the specific folder inside the WhatsApp folder
                }

            }
        }
        else
            Toast.makeText(getContext(), "Please Allow Permissions . Press \" Use This Folder \" ", Toast.LENGTH_LONG).show();


    }

    private void AskPermissionToPeepInsideWhatsAppFolder() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        Uri uri;
        uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);

        try {
            startActivityForResult(intent, 10001);
        } catch (Exception e) {
            Toast.makeText(getContext(), "WhatsApp Not Installed.", Toast.LENGTH_SHORT).show();
        }

    }

    public void openWhatsApp(){

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Main"));

        startActivity(intent);

    }

    }


