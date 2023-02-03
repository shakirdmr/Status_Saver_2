package com.shawlabs.statussaver.Adapters;

import static android.os.Build.VERSION.SDK_INT;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shawlabs.statussaver.MainActivity;
import com.shawlabs.statussaver.R;
import com.shawlabs.statussaver.Utilities;
import com.shawlabs.statussaver.modal.satusModal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FilesGrabberAdapter extends RecyclerView.Adapter<FilesGrabberAdapter.ViewHolder> {
    private List<satusModal> mFiles;
    Context context;
    ContentResolver contentResolver;

int x=0;

    public FilesGrabberAdapter(Context context) {
        mFiles = new ArrayList<>();
        this.context = context;
        contentResolver =  context.getContentResolver();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView statusBox,download,share,playbutton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusBox = itemView.findViewById(R.id.statusBox);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            playbutton = itemView.findViewById(R.id.playbutton);
        }
    }


    public void setFiles(List<satusModal> files) {
        mFiles = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statusviewmodel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        boolean alreadyDownloaded =  checkIfFileExsists(holder,holder.getAdapterPosition(),mFiles.get(position).getName());

        //IMAGE FOUND
        if(mFiles.get(position).getName().endsWith(".jpg"))
        {

        //check if itz in cache aleady
        Bitmap bitmap = MainActivity.getBitmapFromMemoryCache(mFiles.get(position).getName());

        if(bitmap != null) {    //already in cache
            holder.statusBox.setImageBitmap(bitmap);

        }
        else { //insert into cache

            InputStream inputStream = null;
            try {


                inputStream = contentResolver.openInputStream(mFiles.get(position).getUri());
                 bitmap = BitmapFactory.decodeStream(inputStream);

                holder.statusBox.setImageBitmap(bitmap);


                if(mFiles.get(position).getName() !=null && bitmap !=null)
                MainActivity.setBitmapToMemoryCache(mFiles.get(position).getName(), bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "error with file", Toast.LENGTH_SHORT).show();
            }

        }

        x++;} //END IF -- VIDEO FOUND
         else
        {    //VIDEO FOUND

          Glide.with(context).load(mFiles.get(position).getUri()).into(holder.statusBox);
          holder.playbutton.setVisibility(View.VISIBLE);

        }


        holder.statusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BoxIsClicked(position);

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type;

                Uri path  = mFiles.get(position).getUri();
                if(path.toString().endsWith(".mp4") || path.toString().endsWith(".gif"))
                    type = "video/mp4";
                else
                    type = "image/jpg";

                shareFile(type,path );
            }
        });

        //for downloading
        if(!alreadyDownloaded)
            holder.download.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View view) {


                    if(checkIfFileExsists(holder,holder.getAdapterPosition(),mFiles.get(position).getName()))
                        return;


                    //FOR ANDRIOD 11 and gerater
                        if (SDK_INT >= Build.VERSION_CODES.R) {

                            saveFileToGallery(mFiles.get(position).getUri(),mFiles.get(position).getName());
                            checkIfFileExsists(holder,holder.getAdapterPosition(),mFiles.get(position).getName());

                        }
                        else {
                            File downloadFile = new File(mFiles.get(position).getUri().getPath());

                            try {
                                Utilities.checkIsFolderCreated();
                                File destinationPath = new File(Utilities.downloadFolder,""+mFiles.get(position).getName());

                                FileInputStream in = new FileInputStream(downloadFile);
                                FileOutputStream out = new FileOutputStream(destinationPath);


                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);}

                                checkIfFileExsists(holder,holder.getAdapterPosition(),mFiles.get(position).getName());
                                Toast.makeText(context, "Saved  To Gallery ", Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                Log.e("SIMR","--"+e.getMessage());
                                Toast.makeText(context, "Save Failed ", Toast.LENGTH_SHORT).show();
                            }

                        }

                        // Tell the media scanner about the new file so that it is
                        // immediately available to the user.
                        MediaScannerConnection.scanFile(context, new String[]{Utilities.downloadFolder}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {

                                    }
                                });


                }

            });

    } //END OF onBindView



    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    private void BoxIsClicked(int position) {

        if(mFiles.get(position).getUri().toString().endsWith(".jpg")
                || mFiles.get(position).getUri().toString().endsWith(".png")
                || mFiles.get(position).getUri().toString().endsWith(".gif")
                ||  mFiles.get(position).getUri().toString().endsWith(".jpeg"))

            {

                final AlertDialog.Builder alertD = new AlertDialog.Builder(context);

                LayoutInflater inflater = LayoutInflater.from(context);
                View toViewImageFullScreen = inflater.inflate(R.layout.view_image_full_screen, null);
                alertD.setView(toViewImageFullScreen);

                ImageView imageView = toViewImageFullScreen.findViewById(R.id.imageView2020);
                Glide.with(context).load(mFiles.get(position).getUri()).into(imageView);

                AlertDialog alert = alertD.create();
                alert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.show();
            }

        else if(mFiles.get(position).getUri().toString().endsWith(".mp4") )
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
            videoView.setVideoURI(mFiles.get(position).getUri());
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


    private void shareFile(String type, Uri path){


        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType(type);
        it.putExtra(Intent.EXTRA_STREAM, path);
        context.startActivity(Intent.createChooser(it,"Share Using..."));
    }

    private boolean checkIfFileExsists(ViewHolder holder, int position, String name) {

        File checkIfFileIsDownloaded = new File(Utilities.downloadFolder +"/"+name);
        if(checkIfFileIsDownloaded.isFile())
        {


            holder.download.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_download_done_24));
            return true;
        }else return false;
    }

    private void saveFileToGallery(Uri uri, String fileName) {
        try {
            // Open input stream to read file from uri
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            // Set MIME type based on file extension
            String mimeType;
            if (uri.getPath().endsWith(".mp4")) {
                mimeType = "video/mp4";
            }
            else if (uri.getPath().endsWith(".gif")) {
                mimeType = "image/gif";
            } else {
                mimeType = "image/jpeg";
            }

            // Create content values and add file to the device's gallery
            ContentValues values = new ContentValues();
            values.put("_display_name", fileName);
            values.put("mime_type", mimeType);

            values.put("relative_path",Environment.DIRECTORY_DOWNLOADS+"/Status Saver");
            Uri galleryUri = context.getContentResolver()
                    .insert(MediaStore.Files
                            .getContentUri("external_primary"), values);

            // Open output stream to write file to the gallery
            OutputStream outputStream = context.getContentResolver().openOutputStream(galleryUri);

            // Copy file from input stream to output stream
            FileUtils.copy(inputStream, outputStream);


            // Close streams and show success message
            outputStream.close();

            Toast.makeText(context, "File Saved To Gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Show error message if an exception is thrown
            Toast.makeText(context, "Download Failed...", Toast.LENGTH_SHORT).show();
        }
    }
}