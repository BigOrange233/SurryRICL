package com.example.cmpt276group05.callback;

/*
* download stuff
* */
public interface DownloadListener {
    void onStart();//start download
    void onProgress(int progress);//download progress
    void onFinish(String path);//download complete
    void onFail(String errorInfo);//download error
}
