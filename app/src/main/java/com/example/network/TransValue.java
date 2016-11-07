package com.example.network;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.ArrayList;
import java.util.List;

/**
 * �������������һЩȫ�ֹ���ı����ͻ����
 *
 * @author Tony
 */
public class TransValue extends Application {
    private List<String> device_FilesList = new ArrayList<String>();
    private String device_FolderPath;
    private Bitmap bitmap;//�������display activity�е�ͼƬ����ͼ

    private List<String> local_FilesList = new ArrayList<String>();

    //���ͼƬ����ͼ����
    private LruCache<String, Bitmap> mLruCache;


    public void setFilesList(String folderPath, List<String> files_list) {
        this.device_FolderPath = folderPath;
        this.device_FilesList = files_list;
    }

    public List<String> getFilesList() {
        return device_FilesList;
    }

    public String getFolderPath() {
        return device_FolderPath;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void initMyLruCache() {
        int maxMemery = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemery / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // TODO Auto-generated method stub
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public LruCache<String, Bitmap> getMyLruCache() {
        return mLruCache;
    }

    public void setLocalFilesList(List<String> localFilesList) {
        this.local_FilesList = localFilesList;
    }

    public List<String> getLocalFilesList() {
        return local_FilesList;
    }
}
