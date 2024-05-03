package com.example.cmpt276group05.filter;

import android.text.TextUtils;

import com.example.cmpt276group05.callback.SearchListener;
import com.example.cmpt276group05.model.FavManager;
import com.example.cmpt276group05.model.FilterDto;
import com.example.cmpt276group05.model.Restaurant;
import com.example.cmpt276group05.model.RestaurantDto;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
// filter with different situations
public class TextFilter extends BaseFilter<RestaurantDto> {

    public TextFilter(List<RestaurantDto> originData, SearchListener listener) {
        super(originData, listener);
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        String searchContent = charSequence.toString();
        FilterDto filterDto = new Gson().fromJson(searchContent, FilterDto.class);
        FilterResults filterResults = new FilterResults();
        List<RestaurantDto> nameFileterList = new ArrayList<>();
        List<RestaurantDto> favouriteFilterList = new ArrayList<>();
        List<RestaurantDto> criticalFilterList = new ArrayList<>();
        List<RestaurantDto> hazardFilterList = new ArrayList<>();
        if (filterDto == null) {
            criticalFilterList.addAll(originData);
        } else {
            //filter by name
            if (!TextUtils.isEmpty(filterDto.getName())) {
                for (Iterator<RestaurantDto> iterator = originData.iterator(); iterator.hasNext(); ) {
                    RestaurantDto restaurant = iterator.next();
                    String name = restaurant.getName();
                    if (name.toLowerCase().indexOf(filterDto.getName().toLowerCase()) != -1) {
                        nameFileterList.add(restaurant);
                    }
                }
            } else {
                nameFileterList.addAll(originData);
            }

            //filter by favourite
            if (filterDto.isFavourite()) {
                for (Restaurant restaurant : FavManager.getInstance().getFav()) {
                    for (RestaurantDto restaurantDto : nameFileterList) {
                        if (restaurant.getTrackingNumber().equals(restaurantDto.getTrackingNumber())) {
                            favouriteFilterList.add(restaurantDto);
                            break;
                        }
                    }
                }
            } else {
                favouriteFilterList.addAll(nameFileterList);
            }

            //filter by circal number
            if (!TextUtils.isEmpty(filterDto.getCriticalNumber())) {
                for (RestaurantDto restaurantDto : favouriteFilterList) {
                    try {
                        if (Integer.parseInt(restaurantDto.getIssue()) <= Integer.parseInt(filterDto.getCriticalNumber())) {
                            criticalFilterList.add(restaurantDto);
                        }
                    } catch (Exception e) {
                        criticalFilterList.add(restaurantDto);
                    }

                }
            } else {
                criticalFilterList.addAll(favouriteFilterList);
            }

            //filter by hazard level
            if (!TextUtils.isEmpty(filterDto.getHazardLevel())) {
                for (RestaurantDto restaurantDto : criticalFilterList) {
                    if (!TextUtils.isEmpty(restaurantDto.getHazard()) && restaurantDto.getHazard().equals(filterDto.getHazardLevel())) {
                        hazardFilterList.add(restaurantDto);
                    } else if (TextUtils.isEmpty(restaurantDto.getHazard())) {
                        hazardFilterList.add(restaurantDto);
                    }
                }
            } else {
                hazardFilterList.addAll(criticalFilterList);
            }
        }

        filterResults.values = hazardFilterList;
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        currentData.clear();
        if (filterResults.values != null) {
            currentData.addAll((List<RestaurantDto>) filterResults.values);
        }
        searchListener.onFinish(currentData);
    }
}
