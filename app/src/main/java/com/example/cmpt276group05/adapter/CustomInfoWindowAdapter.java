package com.example.cmpt276group05.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintSet;

import com.example.cmpt276group05.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


/*
 * Adapter for information window in map marker
 */

// Src: CodingWithMitch Tutorial - https://youtu.be/DhYofrJPzlI
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
    }

    public void renderWindowText(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvtitle = (TextView) mWindow.findViewById(R.id.title);
        TextView tvsnippet = (TextView) mWindow.findViewById(R.id.snippet);
        if (!title.equals("")) {
            tvtitle.setText(title);
        }

//        Log.d("Tester", marker.getSnippet());
        String snippet = marker.getSnippet();

        try {
            if (!snippet.equals("")) {
                tvsnippet.setText(snippet);
            }
        } catch (NullPointerException e) {
            tvsnippet.setText("N/A");
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }


}
