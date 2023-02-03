    package com.shawlabs.statussaver.Fragments;

    import android.app.ActivityManager;
    import android.app.AlertDialog;
    import android.content.ActivityNotFoundException;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.graphics.Color;
    import android.graphics.drawable.ColorDrawable;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.Menu;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.Window;
    import android.widget.Button;
    import android.widget.CompoundButton;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatDelegate;
    import androidx.appcompat.widget.SwitchCompat;
    import androidx.fragment.app.Fragment;

    import com.bumptech.glide.Glide;
    import com.shawlabs.statussaver.MyForegroundService;
    import com.shawlabs.statussaver.R;


    import java.io.File;

    public class MoreOptionsFragment extends Fragment {

    SwitchCompat newStatusNotification, darkMode;
     TextView whatsappWeb, contact;
    LinearLayout  clearCache_box,howtoUse_box;
    SharedPreferences sharedPreferences;
    Button share,rate;

    public MoreOptionsFragment() {
        // Required empty public constructor
    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_options, container, false);

        newStatusNotification = view.findViewById(R.id.newStatusNotification);
        darkMode = view.findViewById(R.id.darkMode);
        whatsappWeb = view.findViewById(R.id.WhatsAppWeb);
        contact = view.findViewById(R.id.Contact);
        clearCache_box = view.findViewById(R.id.clearCache_box);
        howtoUse_box = view.findViewById(R.id.howtoUse_box);
            share = view.findViewById(R.id.share);
            rate = view.findViewById(R.id.rate);

        sharedPreferences = getContext().getSharedPreferences("NIGHT_OR_DAY",Context.MODE_PRIVATE);

            sharedPreferences = getContext().getSharedPreferences("NIGHT_OR_DAY", Context.MODE_PRIVATE);
            if(sharedPreferences.getBoolean("NIGHT_DAY_SELECTED",false)) {
                darkMode.setChecked(true);
                darkMode.setText("Dark Theme (on)");
            }

            ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            {
                if (MyForegroundService.class.getName().equals(service.service.getClassName())) {

                    newStatusNotification.setChecked(true);
                }}


        howtoUse_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_howto_use_APP();
            }
        });

        clearCache_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearCache(getContext());
            }
             });

        newStatusNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                    MyForegroundService.startService(getContext());
            }
        });

        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

             SharedPreferences.Editor editor = sharedPreferences.edit();
          if(b)
          {
              AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
              darkMode.setText("Dark Theme (on)");
              editor.putBoolean("NIGHT_DAY_SELECTED",true);
          }
          else {
              AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
              darkMode.setText("Dark Theme (off)");
              editor.putBoolean("NIGHT_DAY_SELECTED",false);
          }
          editor.apply();


         }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send_Email_to_myself();
            }
        });

        whatsappWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://web.whatsapp.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareApp();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rateApp();
            }
        });

setHasOptionsMenu(true);

        return view;
    }//END -  ON CREATE

        private void show_howto_use_APP() {
            final AlertDialog.Builder alertD = new AlertDialog.Builder(getContext());

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View toViewImageFullScreen = inflater.inflate(R.layout.view_image_full_screen, null);
            alertD.setView(toViewImageFullScreen);

            ImageView imageView = toViewImageFullScreen.findViewById(R.id.imageView2020);
            Glide.with(getContext()).load(R.drawable.howto_use_app).into(imageView);

            AlertDialog alert = alertD.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alert.show();
        }

        private void send_Email_to_myself() {

        String to = "aly66416@gmail.com";
        String subject = "Make My App/Website";
        String message = "Hello. I want you to make my App/Website as ...";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});

        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

//need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));

    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.stateOfApp).setVisible(false);
    }

    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
            Toast.makeText(context, "Cache cleared successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "An error occurred while clearing the cache.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

        private void shareApp() {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Hey!\n Check out this great app I use to download WhatsApp /WA Business Statuses. \n\n "+"https://play.google.com/store/apps/details?id=com.shawlabs.statussaver");
            startActivity(Intent.createChooser(intent,"Share via..."));


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
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getActivity().getPackageName())));
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

    }
