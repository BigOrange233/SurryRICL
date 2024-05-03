//citation:https://www.youtube.com/watch?v=U6Z8FkjGEb4
//how to customize marker

package com.example.cmpt276group05.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.cmpt276group05.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<MyClusterItem> {

    private final IconGenerator iconGenerator;
    private final ImageView imageview;
    private final int markerWidth;
    private final int markerHeight;



    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageview = new ImageView(context.getApplicationContext());
        markerWidth = (int)context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int)context.getResources().getDimension(R.dimen.custom_marker_image);
        imageview.setLayoutParams(new ViewGroup.LayoutParams(markerWidth,markerHeight));
        int padding = (int)context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageview.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageview);
    }
    @Override
    protected void onBeforeClusterItemRendered(@NonNull MyClusterItem item, @NonNull MarkerOptions markerOptions) {
        if (item.getHazardLevel().contains("High")) {
            imageview.setImageResource(R.drawable.high_bmp);
        } else if (item.getHazardLevel().contains("Moderate")) {
            imageview.setImageResource(R.drawable.mid_bmp);
        } else if (item.getHazardLevel().contains("Low")) {
            imageview.setImageResource(R.drawable.low_bmp);
        } else {
            imageview.setImageResource(R.drawable.low_bmp);
        }

        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    /**
     * Determine whether the cluster should be rendered as individual markers or a cluster.
     *
     * @param cluster cluster to examine for rendering
     * @return true if the provided cluster should be rendered as a single marker on the map, false
     * if the items within this cluster should be rendered as individual markers instead.
     */
    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<MyClusterItem> cluster) {
        return cluster.getSize() > 2;
    }
}
