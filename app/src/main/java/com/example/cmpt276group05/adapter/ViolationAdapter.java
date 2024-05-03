package com.example.cmpt276group05.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.example.cmpt276group05.R;
import com.example.cmpt276group05.model.Violation;

import java.util.List;

/*
* violation adapter display violation item
* */
public class ViolationAdapter extends CommonAdapter<Violation> {

    public ViolationAdapter(Context context, List<Violation> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Violation item) {
        super.convert(holder, item);
        if(!TextUtils.isEmpty(item.getCirtical()) && item.getCirtical().equals("Critical")){
            holder.setImageDrawable(R.id.iv_cirtical,R.mipmap.cirtical);
        }else{
            holder.setImageDrawable(R.id.iv_cirtical,R.mipmap.no_cirtical);
        }

        if(!TextUtils.isEmpty(item.getDesc()) && item.getDesc().indexOf("Equipment")!=-1){
            holder.getView(R.id.iv_equipment).setVisibility(View.VISIBLE);
            holder.setImageDrawable(R.id.iv_equipment,R.mipmap.equipment);
        }

        if(!TextUtils.isEmpty(item.getDesc()) && item.getDesc().indexOf("utensils")!=-1){
            holder.getView(R.id.iv_pest).setVisibility(View.VISIBLE);
            holder.setImageDrawable(R.id.iv_pest,R.mipmap.utensil);
        }

        if(!TextUtils.isEmpty(item.getDesc()) && item.getDesc().indexOf("food")!=-1){
            holder.getView(R.id.iv_food).setVisibility(View.VISIBLE);
            holder.setImageDrawable(R.id.iv_food,R.mipmap.food);
        }

        holder.setText(R.id.tv_desc,item.getDesc());
    }
}
