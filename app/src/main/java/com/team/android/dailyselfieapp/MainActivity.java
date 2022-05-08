package com.team.android.dailyselfieapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_SELFIE_REQUEST_CODE = 1;
    private static final String LAST_PHOTO_PATH = "mLastPhotoPath";
    private static final String LAST_PHOTO_DATE = "mLastPhotoDate";
    private AdapterGridView mAdapterGridView;
    private String mLastPhotoPath = "";
    private long mLastSelfieTime = new Date().getTime();


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, getString(R.string.no_external_storage), Toast.LENGTH_SHORT).show();
            finish();
        }

        if (savedInstanceState != null) {
            mLastPhotoPath = savedInstanceState.getString(LAST_PHOTO_PATH);
            mLastSelfieTime = savedInstanceState.getLong(LAST_PHOTO_DATE);
        }
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView != null) {
            mAdapterGridView = new AdapterGridView(this);
            gridView.setAdapter(mAdapterGridView);
        }
        mAdapterGridView.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_camera):
                Log.i(Constains.TAG, "Action Camera Clicked");
                takeNewSelfie();
                return true;
            case (R.id.action_delete_all):
                Log.i(Constains.TAG, "Delete all Clicked");
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.dialog_delete_all_title))
                        .setMessage(getString(R.string.dialog_delete_all_text))
                        .setPositiveButton(getString(R.string.positive_answer), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapterGridView.deleteAll();
                            }

                        })
                        .setNegativeButton(getString(R.string.negative_answer), null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void takeNewSelfie() {
        mLastSelfieTime = new Date().getTime();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String imageFileName = "selfie_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(mLastSelfieTime);
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constains.ALBUM_NAME);
            storageDir.mkdirs();
            File photoFile = new File(storageDir.getAbsolutePath() + File.separator + imageFileName + ".png");

            mLastPhotoPath = photoFile.getAbsolutePath();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                startActivityForResult(takePictureIntent, TAKE_SELFIE_REQUEST_CODE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_SELFIE_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), getString(R.string.action_photo_save), Toast.LENGTH_SHORT).show();
            Selfie selfie = new Selfie(mLastSelfieTime, Uri.fromFile(new File(mLastPhotoPath)));
            mAdapterGridView.add(selfie);
            try {
                FileOutputStream fos = new FileOutputStream(mLastPhotoPath);
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
                fos.write(bytearrayoutputstream.toByteArray());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(LAST_PHOTO_PATH, mLastPhotoPath);
        savedInstanceState.putLong(LAST_PHOTO_DATE, mLastSelfieTime);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void showViewer(Uri uri) {
        Intent intent = new Intent(this, ActivityImageViewer.class);
        Bundle b = new Bundle();
        b.putParcelable("URI", uri);
        intent.putExtras(b);
        startActivity(intent);
    }

}