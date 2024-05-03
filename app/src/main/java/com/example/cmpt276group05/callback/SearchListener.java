package com.example.cmpt276group05.callback;

import java.util.List;
//search restaurant listener
public interface SearchListener<T> {
    public void onFinish(List<T> dataList);
}
