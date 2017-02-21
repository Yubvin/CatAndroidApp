package com.catandroidapp.services;

import android.util.Log;

import com.catandroidapp.models.Cat;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by mac on 21.02.17.
 */

public class CatService {

    private static final String URL = "http://192.168.1.9:8080/";
    private Retrofit mRetrofit;
    private CatApi mService;

    public CatService(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mService = mRetrofit.create(CatApi.class);
    }

    public Observable<List<Cat>> getCats(){
        return mService.getCats();
    }

    public Observable<Response<ResponseBody>> getCatImgURL(String filename){
        return mService.getCatImgURL(filename);
    }

    public interface CatApi {

        @GET("rest/cat/")
        public Observable<List<Cat>>
        getCats();

        @Streaming
        @GET("rest/cat/files/{filename}")
        public Observable<Response<ResponseBody>>
        getCatImgURL(@Path("filename") String filename);
    }
}
