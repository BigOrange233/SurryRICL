package com.example.cmpt276group05.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmpt276group05.R;
import com.example.cmpt276group05.model.FavManager;
import com.example.cmpt276group05.model.FilterDto;
import com.example.cmpt276group05.model.Restaurant;
import com.example.cmpt276group05.model.RestaurantDto;
import com.example.cmpt276group05.ui.InspectionList;
import com.example.cmpt276group05.ui.MainActivity;

import java.util.List;
/*
 * restaurant adapter display restaurant item
 * */
public class RestaurantAdapter extends CommonAdapter<RestaurantDto> {
    int presetimages[] = {R.mipmap.aw,R.mipmap.fi,R.mipmap.fs,R.mipmap.bp,R.mipmap.se,
            R.mipmap.bc,R.mipmap.juice,R.mipmap.bs,R.mipmap.dq,R.mipmap.panago};
    int images[] = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four};
    int THazardI[] = {R.mipmap.low, R.mipmap.mid, R.mipmap.high};
    int favIcons[] = {R.mipmap.none,R.mipmap.favorite};

    public RestaurantAdapter(Context context, List<RestaurantDto> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, RestaurantDto item) {
        super.convert(holder, item);
        FilterDto filterDto = new FilterDto();
        ImageView resIcon = holder.getView(R.id.image);
        TextView name = holder.getView(R.id.name);
        TextView dates = holder.getView(R.id.date);
        TextView issues = holder.getView(R.id.issue);
        ImageView HazardIcons = holder.getView(R.id.hazardicon);
        ImageView favorite = holder.getView(R.id.favicon);
        resIcon.setImageResource(images[item.getIndex()%4]);
        if(item.getName().contains("Boston")){
            resIcon.setImageResource(presetimages[3]);
        }else if (item.getName().contains("Eleven")){
            resIcon.setImageResource(presetimages[4]);
        }else if (item.getName().contains("A&W")){
            resIcon.setImageResource(presetimages[0]);
        }else if (item.getName().contains("Freshii")){
            resIcon.setImageResource(presetimages[1]);
        }else if (item.getName().contains("Freshslice")){
            resIcon.setImageResource(presetimages[2]);
        }else if (item.getName().contains("A & W")){
            resIcon.setImageResource(presetimages[0]);
        }else if (item.getName().contains("Blenz")){
            resIcon.setImageResource(presetimages[5]);
        }else if (item.getName().contains("Booster")){
            resIcon.setImageResource(presetimages[6]);
        }else if (item.getName().contains("Browns")){
            resIcon.setImageResource(presetimages[7]);
        }else if (item.getName().contains("Dairy")){
            resIcon.setImageResource(presetimages[8]);
        }else if (item.getName().contains("Panago")){
            resIcon.setImageResource(presetimages[9]);
        }
        name.setText(item.getName());
        dates.setText(context.getString(R.string.latest_inspection) + item.getDate());
        try{
            if(FavManager.getInstance().contains_tracking_number(item.getTrackingNumber())){
                favorite.setImageResource(favIcons[1]);
            }else{
                favorite.setImageResource(favIcons[0]);
            }
        }catch (Exception e){
                favorite.setImageResource(favIcons[0]);
        }
        issues.setText(context.getString(R.string.number_of_issues_found) + item.getIssue());

        try{
            if (item.getHazard().equals("Low")) {
                HazardIcons.setImageResource(THazardI[0]);
            } else if (item.getHazard().equals("Moderate")) {
                HazardIcons.setImageResource(THazardI[1]);
            } else if (item.getHazard().equals("High")) {
                HazardIcons.setImageResource(THazardI[2]);
            }
        }catch (Exception e){
            HazardIcons.setImageResource(THazardI[0]);
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InspectionList.class);
                //please put your activities name on the class above!
                intent.putExtra("Tracking_Number", item.getTrackingNumber());
                context.startActivity(intent);
            }
        });
    }


}
