package com.team.android.dailyselfieapp;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Selfie {
    private final long mId;
    private final String mName;
    private final long mTime;
    private final Uri mImageUri;

    public Selfie(long mTime, Uri mImageUri) {
        this.mId = mTime;
        this.mName = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(mTime));
        this.mTime = mTime;
        this.mImageUri = mImageUri;
    }

    public long getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public long getmTime() {
        return mTime;
    }

    public Uri getmImageUri() {
        return mImageUri;
    }
}
