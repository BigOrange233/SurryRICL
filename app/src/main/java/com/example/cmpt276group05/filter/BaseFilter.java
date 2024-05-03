package com.example.cmpt276group05.filter;

import android.widget.BaseAdapter;
import android.widget.Filter;

import com.example.cmpt276group05.callback.SearchListener;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFilter<T> extends Filter {
    public List<T> currentData;
    public List<T> originData = new ArrayList<>();
    public SearchListener searchListener;

    public BaseFilter(List<T> originData,SearchListener listener) {
        this.currentData = originData;
        this.originData.addAll(originData);
        this.searchListener = listener;
    }

    public List<T> getOriginData() {
        return originData;
    }

    public void setOriginData(List<T> originData) {
        this.originData.clear();
        this.originData.addAll(originData);
    }

    public List<T> getCurrentData() {
        return currentData;
    }

    public void setCurrentData(List<T> currentData) {
        this.currentData = currentData;
    }
}
