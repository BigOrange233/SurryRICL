package com.example.cmpt276group05.utils;

import android.text.TextUtils;

import com.example.cmpt276group05.callback.DownloadListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
* file commom function
*
* */
public class FileUtils {

    /*
    * generate csv file from net stream
    * */
    public static void generateCsvFileFromStream(String filePath, InputStream is,
                                                 long totalLength, DownloadListener downloadListener){
        if(downloadListener!=null){
            downloadListener.onStart();
        }

        if(TextUtils.isEmpty(filePath)){
            return;
        }

        File distFile = new File(filePath);
        if (!distFile.exists()) {
            if (!distFile.getParentFile().exists()){
                distFile.getParentFile().mkdirs();
            }
            try {
                distFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }

        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(distFile));
            byte data[] = new byte[2048];
            int len;
            while ((len = is.read(data, 0, 2048)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
            }
            downloadListener.onFinish(distFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
