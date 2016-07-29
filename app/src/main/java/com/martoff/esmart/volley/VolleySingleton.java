package com.martoff.esmart.volley;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.martoff.esmart.AppController;

/**
 * Created by Safeer on 14-May-16.
 */
public class VolleySingleton {

    RequestQueue request_queue;
    private static VolleySingleton volley_instance = null;

    ImageLoader image_loader;

    private VolleySingleton() {
        request_queue = Volley.newRequestQueue(AppController.getAppContext());
        image_loader = new ImageLoader(request_queue, new ImageLoader.ImageCache() {

            LruCache<String, Bitmap> cache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 1024 / 8));

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static VolleySingleton getInstance() {
        if (volley_instance == null) {
            volley_instance = new VolleySingleton();
        }
        return volley_instance;
    }

    public RequestQueue getRequestQueue() {
        return request_queue;
    }

    public ImageLoader getImage_loader(){
        return image_loader;
    }
}