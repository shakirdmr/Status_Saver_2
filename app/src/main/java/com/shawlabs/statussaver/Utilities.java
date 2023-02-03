package com.shawlabs.statussaver;

import static android.os.Build.VERSION.SDK_INT;

import android.os.Build;
import android.os.Environment;

import java.io.File;

public class Utilities {

    public static String downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "Status Saver";
    public static Boolean is_below_android_11 = SDK_INT < Build.VERSION_CODES.R;

    public static void checkIsFolderCreated() {
        if(!new File(downloadFolder).isDirectory())
            new File(downloadFolder).mkdirs();
    }
}