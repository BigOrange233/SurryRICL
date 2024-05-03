package com.example.cmpt276group05.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ViewHoloder is BaseHoloder to convert data to interface
 */

public class ViewHolder {
    public static final String TAG = "ViewHolder";
    private final SparseArray<View> views;
    private int position;
    private View convertView;
    private Context context;

    private ViewHolder(Context context, ViewGroup viewGroup, int layoutId, int position){
        this.context = context;
        this.views = new SparseArray<>();
        this.convertView = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId,viewGroup,false);
        convertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup viewGroup, int layoutId, int position){
        if(convertView==null){
            return new ViewHolder(context,viewGroup,layoutId,position);
        }
        return (ViewHolder) convertView.getTag();
    }

    public <T extends View> T getView(int viewId){
        View view = views.get(viewId);
        if(view == null){
            view = convertView.findViewById(viewId);
            views.put(viewId,view);
        }
        return (T) view;
    }

    public View getConvertView(){
        return convertView;
    }

    public ViewHolder setText(int viewId,String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public ViewHolder setTextColor(int viewId,int color){
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }

    public ViewHolder setImageByUrl(final int viewId, String url){
        final ImageView iv = getView(viewId);
//        Glide.with(context).load(url).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.ALL).into(iv);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId,Bitmap bitmap){
        ImageView iv = getView(viewId);
        iv.setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setImageDrawable(int viewId,int drawableId){
        ImageView iv = getView(viewId);
        iv.setImageDrawable(context.getResources().getDrawable(drawableId));
        return this;
    }

    public int getPosition(){
        return position;
    }

}
