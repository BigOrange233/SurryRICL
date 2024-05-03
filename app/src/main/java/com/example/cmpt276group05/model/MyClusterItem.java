package com.example.cmpt276group05.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/*
 * Custom Data class for cluster manager
 */

public class MyClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    private final String hazardLevel;
    private final String trackingNumber;

    public MyClusterItem(LatLng position, String title, String snippet, String hazardLevel, String trackingNumber) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.hazardLevel = hazardLevel;
        this.trackingNumber = trackingNumber;
    }


    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }
}
