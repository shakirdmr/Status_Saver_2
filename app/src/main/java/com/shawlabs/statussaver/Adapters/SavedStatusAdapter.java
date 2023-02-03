package com.shawlabs.statussaver.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shawlabs.statussaver.R;
import com.shawlabs.statussaver.Utilities;


import java.io.File;
import java.util.ArrayList;


public class SavedStatusAdapter extends RecyclerView.Adapter<SavedStatusAdapter.WhatsAppViewHolder> {

    private ArrayList<File> myList;
    private Context context;


    public SavedStatusAdapter(ArrayList<File> myList, Context context) {
        this.myList = myList;
        this.context = context;
    }


    @NonNull
    @Override
    public WhatsAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lay =  LayoutInflater.from(parent.getContext());

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statusviewmodel, parent, false);

        return new WhatsAppViewHolder(view);

    }



    @Override
    public void onBindViewHolder(@NonNull WhatsAppViewHolder holder, int position) {

        File item = myList.get(position);

        if(item.toString().endsWith(".mp4"))
            holder.playButton.setVisibility(View.VISIBLE);


        Glide.with(context).load(item.getPath()).into(holder.statusBox);


        //For Deleting
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
               // builder.setMessage("Are you sure you want to delete?") .setTitle("Delete status");

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete?")
                        .setTitle("Delete Status")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                final String path = item.getPath();

                                File downloadFile = new File(path);

                                if (downloadFile.delete()) {
                                    Toast.makeText(context, "Your File Is Deleted.", Toast.LENGTH_SHORT).show();
                                    //context.startActivity(get);
                                    removedTheItem(position);
                                } else
                                    Toast.makeText(context, "Can't Delete. Something Went Wrong.", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }


        });


        //for sharing
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type,path;

                path  = item.getPath();
                if(item.getPath().endsWith(".mp4") || item.getPath().endsWith(".gif"))
                    type = "video/mp4";
                else
                    type = "image/jpg";

                shareFile(type,path );

            }
        });

//IF IMAGE/VIDEO IS CLICKED
        holder.statusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(item.getPath().endsWith(".jpeg") || item.getPath().endsWith(".png")
                        || item.getPath().endsWith(".gif") ||item.getPath().endsWith(".jpg"))
                {


                    final AlertDialog.Builder alertD = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View toViewImageFullScreen = inflater.inflate(R.layout.view_image_full_screen, null);
                    alertD.setView(toViewImageFullScreen);

                    ImageView imageView = toViewImageFullScreen.findViewById(R.id.imageView2020);
                    Glide.with(context).load(item.getPath()).into(imageView);

                    AlertDialog alert = alertD.create();
                    alert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.show();
                }
                else if(item.getPath().endsWith(".mp4"))
                {

                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View view1 = inflater.inflate(R.layout.view_video_full_screen, null);


                    final AlertDialog.Builder alertDg = new AlertDialog.Builder(context);

                    FrameLayout mediaControls = view1.findViewById(R.id.videoViewWrapper);

                    if (view1.getParent() != null) {
                        ((ViewGroup) view1.getParent()).removeView(view1);
                    }

                    alertDg.setView(view1);

                    final VideoView videoView = view1.findViewById(R.id.video_full);

                    final MediaController mediaController = new MediaController(context, false);

                    videoView.setOnPreparedListener(mp -> {

                        mp.start();
                        mediaController.show(0);
                        mp.setLooping(true);
                    });

                    videoView.setMediaController(mediaController);
                    mediaController.setMediaPlayer(videoView);
                    videoView.setVideoURI(Uri.parse(item.getPath()));
                    videoView.requestFocus();

                    ((ViewGroup) mediaController.getParent()).removeView(mediaController);

                    if (mediaControls.getParent() != null) {
                        mediaControls.removeView(mediaController);
                    }

                    mediaControls.addView(mediaController);

                    final AlertDialog alert2 = alertDg.create();

                    alert2.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    alert2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alert2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    alert2.show();

                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return myList.size();
    }





    public  class WhatsAppViewHolder extends RecyclerView.ViewHolder {

ImageView statusBox, delete,share,playButton;

        public WhatsAppViewHolder(@NonNull View itemView) {
            super(itemView);
            statusBox = itemView.findViewById(R.id.statusBox);
            delete = itemView.findViewById(R.id.download);
            delete.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_delete_forever_24));
            share = itemView.findViewById(R.id.share);
            playButton = itemView.findViewById(R.id.playbutton);
        }
    }


    private void removedTheItem(int position) {
        myList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,myList.size());

        MediaScannerConnection.scanFile(context, new String[]{Utilities.downloadFolder}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    private void shareFile(String type, String path){
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType(type);
        it.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        context.startActivity(Intent.createChooser(it,"Share Using..."));
    }

}
