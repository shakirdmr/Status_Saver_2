package com.shawlabs.statussaver.modal;

import android.net.Uri;

public class satusModal {
    private String fileName;
    private Uri uri;


    public String getName() {
        return fileName;
    }

    public void setName(String fileName) {
        this.fileName = fileName;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public satusModal(String fileName, Uri uri) {
        this.fileName = fileName;
        this.uri = uri;
    }

}
