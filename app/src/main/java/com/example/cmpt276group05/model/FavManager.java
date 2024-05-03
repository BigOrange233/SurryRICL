package com.example.cmpt276group05.model;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.cmpt276group05.constant.BusinessConstant;
import com.example.cmpt276group05.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FavManager{
    private List<Restaurant> fav = new ArrayList<>();
    private boolean isInited = false;
    private static FilterDto isfavorite = new FilterDto();

    private static FavManager instance;
    private FavManager(){}

    public static FavManager getInstance(){
        if(instance == null){
            instance = new FavManager();
        }
        return  instance;
    }
    public static void setFav(boolean yn){
        isfavorite.setFavourite(yn);
    }

    public static boolean isFav(){

        return isfavorite.isFavourite();
    }
    public void addRestaurant(Restaurant r){
        fav.add(r);
    }

    public void removeRestaurant(Restaurant r){
        Restaurant remove=null;
        for(Restaurant restaurant : fav){
            if(restaurant.getTrackingNumber().equals(r.getTrackingNumber())){
                remove = restaurant;
            }
        }
        if(remove!=null){
            fav.remove(remove);
        }
    }

    public Boolean contains_tracking_number(String trackingNumber){
        for(Restaurant r: fav){
            if(r.getTrackingNumber().equals(trackingNumber))
                return true;
        }
        return false;
    }
    //init sp
    public void init(Context context){
        if(!isInited){
            String history = SPUtils.get(context, BusinessConstant.FAV,"");
            if(!TextUtils.isEmpty(history)){
                Type type =new TypeToken<List<Restaurant>>() {}.getType();
                fav.addAll(new Gson().fromJson(history, type));
            }
            isInited = true;
        }
    }
    //update sp
    public void saveFavIntoSp(Context context){
        SPUtils.put(context, BusinessConstant.FAV,new Gson().toJson(fav));
    }

    public  List<Restaurant> getFav() {
        return fav;
    }

}
