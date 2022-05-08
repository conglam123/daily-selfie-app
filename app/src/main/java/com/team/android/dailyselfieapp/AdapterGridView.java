package com.team.android.dailyselfieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapterGridView extends BaseAdapter {
    private final Context mContext;
    private List<Selfie> mItems = new ArrayList<Selfie>();
    private LayoutInflater mInflater;

    public AdapterGridView(Context mContext) {
        this.mContext = mContext;
        this.mInflater =LayoutInflater.from(mContext);

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constains.ALBUM_NAME);
        storageDir.mkdirs();
        File[] files = storageDir.listFiles();

        for (File f : files) {
            this.mItems.add(new Selfie(f.lastModified(), Uri.fromFile(f)));
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return this.mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.mItems.get(i).getmId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        SquareImageView picture;
        TextView name;

        if(v == null) {
            v = this.mInflater.inflate(R.layout.grid_view, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (SquareImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        final Selfie item = mItems.get(i);
        Log.i("Debug", item.getmImageUri().toString());

        Picasso.with(this.mContext)
                .load(item.getmImageUri())
                .fit()
                .centerInside()
                .into(picture);

        name.setText(item.getmName());

        v.setOnLongClickListener(new View.OnLongClickListener() {
            private Selfie selfie = item;

            @Override
            public boolean onLongClick(View v) {
                Log.i(Constains.TAG, "Deleting Selfie.");
                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(mContext.getString(R.string.dialog_delete_single_title))
                        .setMessage(mContext.getString(R.string.dialog_delete_single_message))
                        .setPositiveButton(mContext.getString(R.string.positive_answer), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new File(selfie.getmImageUri().getPath()).delete();
                                mItems.remove(selfie);
                                AdapterGridView.this.notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton(mContext.getString(R.string.negative_answer), null)
                        .show();
                return true;
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            private Uri uri = item.getmImageUri();

            @Override
            public void onClick(View v) {
                Log.i(Constains.TAG, "Opening ImageViewer.");
                ((MainActivity) mContext).showViewer(uri);
            }
        });

        return v;
    }

    public void add(Selfie selfie) {
        Log.i(Constains.TAG, "Adding new Selfie.");
        mItems.add(selfie);
        notifyDataSetChanged();
    }

    public void deleteAll() {
        for (Selfie s : mItems) {
            new File(s.getmImageUri().getPath()).delete();
        }
        mItems.clear();
        this.notifyDataSetChanged();
    }
}
