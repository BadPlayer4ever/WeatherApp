package com.vargas.basicweatherapp.services;

import android.provider.DocumentsContract;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;

import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLRequest;
import cafsoft.foundation.URLSession;
import com.vargas.basicweatherapp.services.models.Root;

public class weatherServices {
    private String urlString="https://api.openweathermap.org/data/2.5/weather";
    private String apiKey;

    public weatherServices(String key){
        this.apiKey=key;
    }

    public void requestData(String cityName, String isoCode, OnResponse delegate){
        URLComponents components= new URLComponents();
        String query= cityName+","+isoCode;
        components.setScheme("https");
        components.setHost("api.openweathermap.org");
        components.setPath("/data/2.5/weather");
        components.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("units","metric"),
                new URLQueryItem("lang","es"),
                new URLQueryItem("q",query),
                new URLQueryItem("appid",this.apiKey)
        });

        URL url=components.getURL();

        URLSession.getShared().dataTask(url, (data,response,error)->{
            HTTPURLResponse resp= (HTTPURLResponse) response;
            Root root= null;

            int statusCode=-1;

            Log.i("STATUS INFO","STATUS CODE: "+resp.getStatusCode());
            if(error==null && resp.getStatusCode()==200){
                String text=data.toText();
                text=text.replace("[","");
                text=text.replace("]","");
                GsonBuilder builder=new GsonBuilder();
                builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                Gson gson= builder.create();
                root=gson.fromJson(text, Root.class);
                statusCode=resp.getStatusCode();
            }
            else if (resp.getStatusCode()==404){
                statusCode=resp.getStatusCode();
            }
            if (delegate!=null){
                delegate.onChange(error!=null,statusCode,root);
            }

        }).resume();
    }

    public interface OnResponse{
        public abstract void onChange(boolean isNetworkError, int statusCode, Root root);
    }

}
