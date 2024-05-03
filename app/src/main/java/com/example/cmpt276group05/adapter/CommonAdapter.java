package com.example.cmpt276group05.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * CommonAdapter is baseAdapter
 * it is extend Android BaseAdapter
 */

public class CommonAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> list;
    protected int layoutId;
    protected LayoutInflater inflater;

    public CommonAdapter(Context context, List<T> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public T getItem(int i) {
        if(list!=null){
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = getViewHolder(i,view,viewGroup);
        convert(holder,getItem(i));
        return holder.getConvertView();
    }

    public  void convert(ViewHolder holder,T item){};

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup viewGroup){
        return ViewHolder.get(context,convertView,viewGroup,layoutId,position);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
