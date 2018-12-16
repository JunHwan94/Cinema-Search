package com.polarbearr.cinemasearch.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpHelper{

    private static HttpHelper instance;
    private RequestQueue requestQueue;
    private static Context context;

    public HttpHelper(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized HttpHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HttpHelper(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}