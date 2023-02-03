package com.shawlabs.statussaver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;


public class MyForegroundService extends Service {


    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "channel_id";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = buildNotification();
       startTheServiceForLookingInFolderChanges();

        startForeground(NOTIFICATION_ID,notification);
        return START_STICKY;

    } //END ON-START



    private Notification buildNotification() {


        Intent notificationIntent =  new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,notificationIntent,0);

        String contentText = "New Statuses will be notified when found";


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Status Saver")
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Check if Android version is Oreo or higher and create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_HIGH));
        }

       return builder.build();

    }

    public static void startService(Context context) {

        //flag 1-notification only
        //     2- autosave

        Intent serviceIntent = new Intent(context, MyForegroundService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    private void startTheServiceForLookingInFolderChanges() {

        //The desired path to watch or monitor
        //E.g Camera folder
        final String pathToWatch = Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";
        
        FileObserver observer = new FileObserver(pathToWatch, FileObserver.CREATE) { // set up a file observer to watch this directory

            @Override
            public void onEvent(int event, final String file) {



                if (event == FileObserver.CREATE || event == FileObserver.CLOSE_WRITE
                        ||event == FileObserver.MODIFY || event == FileObserver.MOVED_TO
                        ||event == FileObserver.OPEN   && !file.equals(".probe"))
                {
                    // check that it's not equal to .probe because thats created every time camera is launched

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            new_notification_for_newStatus();
                                   }
                    });


                }

            }
        };
        observer.startWatching();



    }

    private void new_notification_for_newStatus() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID", "NAME", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("HERE IS THE DESCRIPTION");

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        Intent notificationIntent =  new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,notificationIntent,0);


        NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this,"CHANNEL_ID")
                            .setSmallIcon(R.mipmap.ic_launcher) //set icon for notification
                            .setContentTitle("Status Saver") //set title of notification
                            .setContentText("New Status Found")//this is notification message
                            .setAutoCancel(true) // makes auto cancel of notification
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification

            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

        }


    }
