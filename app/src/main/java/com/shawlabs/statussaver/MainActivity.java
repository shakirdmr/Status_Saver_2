package com.shawlabs.statussaver;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.shawlabs.statussaver.Adapters.MyFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    MaterialToolbar materialToolbar;
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    MyFragmentPagerAdapter fragmentPagerAdapter;
    private static LruCache<String, Bitmap> mMemoryCache;
    Boolean isNightMode = false;

    @Override
    protected void onStart() {
        super.onStart();

        //check for new version
        inAppUpdate();

        sharedPreferences = getSharedPreferences("NIGHT_OR_DAY", Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("NIGHT_DAY_SELECTED",false)) {
            isNightMode = true;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CACHE
        final int xv = (int) Runtime.getRuntime().maxMemory();
        final int maxMemorySize = xv / 1024;
        final int cacheSize = maxMemorySize / 10;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        //CACHE

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        materialToolbar = findViewById(R.id.toolbarMainActivity);

        //
        int color = getResources().getColor(R.color.navigation_bar_color);
        if(!isNightMode) {
            getWindow().setNavigationBarColor(color);
            bottomNavigationView.setBackgroundTintList(ColorStateList.valueOf(color));

        }
        else{
            getWindow().setNavigationBarColor(color);
        }

        //permsiosins

        if(Utilities.is_below_android_11) {
            if (!checkForPermissions())
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                        112233);
        }

        setTheViewPager();

        // Set up a page change listener for the ViewPager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {

                if(position == 0)
                    bottomNavigationView.setSelectedItemId(R.id.nav_item_1);
                else if(position==1)
                    bottomNavigationView.setSelectedItemId(R.id.nav_item_2);
                else if(position==2)
                    bottomNavigationView.setSelectedItemId(R.id.nav_item_3);
                else if(position==3)
                    bottomNavigationView.setSelectedItemId(R.id.nav_item_4);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Toast.makeText(MainActivity.this, "state "+state, Toast.LENGTH_SHORT).show();

            }

        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_item_1:
                        viewPager.setCurrentItem(0);
                        return true;

                    case R.id.nav_item_2:
                        viewPager.setCurrentItem(1);
                        return true;

                    case R.id.nav_item_3:
                        viewPager.setCurrentItem(2);
                        return true;

                    case R.id.nav_item_4:
                        viewPager.setCurrentItem(3);
                        return true;

                    default:
                        return false;
                }
            }
        });

    } //END OF ON - CREATE

    private void setTheViewPager() {

        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        setSupportActionBar(materialToolbar);

    }

    //FOR CACHE  getters,setters
    public static Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }
    public static void setBitmapToMemoryCache(String key, Bitmap bitmap) {

        //check if it ain't in cache already
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    //FOR CACHE  getters,setters

    //check READ - WRITE permisions
    private boolean checkForPermissions() {

        int resultWriteExternal =
                ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        int resultReadExternal =
                ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);

        return resultReadExternal == PackageManager.PERMISSION_GRANTED &&
                resultWriteExternal == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 112233){
            if(grantResults.length>0){

                int write =  grantResults[0];
                int read =  grantResults[1];

                boolean checkWrite = write == PackageManager.PERMISSION_GRANTED;
                boolean checkRead =read == PackageManager.PERMISSION_GRANTED;

                if(checkRead && checkWrite)
                    setTheViewPager();
                else {


                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                            112233);
                }

            }
        }

    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmainactivity, menu);


        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        switch (id) {


            case R.id.howToUse:
                show_howto_use_APP();
                return true;

            case R.id.aboutApp:
                Toast.makeText(this, "    Shaw Labs Corp. \n\n Author: Shakir    ", Toast.LENGTH_LONG).show();
                return true;

            case R.id.shareApp:
                shareApp();
                return true;

            case R.id.rateApp:
                rateApp();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareApp() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey!\n Check out this great app I use to download WhatsApp /WA Business Statuses. \n\n "+"https://play.google.com/store/apps/details?id=com.shawlabs.statusaver");
        startActivity(Intent.createChooser(intent,"Share via..."));


    }

    private void show_howto_use_APP() {
        final AlertDialog.Builder alertD = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View toViewImageFullScreen = inflater.inflate(R.layout.view_image_full_screen, null);
        alertD.setView(toViewImageFullScreen);

        ImageView imageView = toViewImageFullScreen.findViewById(R.id.imageView2020);
        Glide.with(this).load(R.drawable.howto_use_app).into(imageView);

        AlertDialog alert = alertD.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
    }

    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details?id=com.shawlabs.statussaver");
            startActivity(rateIntent);
        }
    }
    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public void inAppUpdate() {

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {


//                requestUpdate(result);
                    android.view.ContextThemeWrapper ctw = new android.view.ContextThemeWrapper(MainActivity.this, R.style.Theme_AppCompat_Light_NoActionBar_FullScreen);

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                    alertDialogBuilder.setTitle("Update Available");
                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

                    alertDialogBuilder.setMessage("Status Saver app has a new version available. \nUpdate now for latest features.");
                    alertDialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                            }
                        }
                    });
                    alertDialogBuilder.show();

                }
            }
        });
    }


}