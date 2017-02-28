package com.catandroidapp.services;

import android.util.Log;

import com.catandroidapp.models.Cat;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by mac on 21.02.17.
 */

public class CatService {

    private static final String URL = "http://localhost:8080/";
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

    public Observable<Cat> getCatById(long id){
        return mService.getCatsById(id);
    }

    public Observable<List<Cat>> getCatsByName(String name){
        return mService.getCatsByName(name);
    }

    public Observable<List<Cat>> getCatsByAge(short age){
        return mService.getCatsByAge(age);
    }

    public Observable<List<Cat>> getCatsByBreed(String breed){
        return mService.getCatsByBreed(breed);
    }

    public Observable<Response<ResponseBody>> getCatImgURL(String filename){
        return mService.getCatImgURL(filename);
    }

    public Observable<Cat> addCat(RequestBody name, RequestBody age, RequestBody breed, MultipartBody.Part image){
        return mService.addCat(name, age, breed, image);
    }

    public Observable<Cat> deleteCat(long id){
        return mService.deleteCat(id);
    }

    public Observable<Cat> updateCat(Cat cat){
        return mService.updateCat(cat);
    }

    public interface CatApi {

        @GET("rest/cat/")
        Observable<List<Cat>>
        getCats();

        @GET("rest/cat/{id}")
        Observable<Cat>
        getCatsById(@Path("id") long id);

        @GET("rest/cat/name/{name}")
        Observable<List<Cat>>
        getCatsByName(@Path("name") String name);

        @GET("rest/cat/age/{age}")
        Observable<List<Cat>>
        getCatsByAge(@Path("age") short age);

        @GET("rest/cat/breed/{breed}")
        Observable<List<Cat>>
        getCatsByBreed(@Path("breed") String breed);

        @DELETE("rest/cat/delete/{id}")
        Observable<Cat>
        deleteCat(@Path("id") long id);

        @PUT("rest/cat/update")
        Observable<Cat>
        updateCat(@Body Cat cat);

        @Streaming
        @GET("rest/cat/files/{filename}")
        Observable<Response<ResponseBody>>
        getCatImgURL(@Path("filename") String filename);

        @Multipart
        @POST("rest/cat/add?")
        Observable<Cat>
        addCat(@Part("name") RequestBody name, @Part("age") RequestBody age, @Part("breed") RequestBody breed, @Part MultipartBody.Part image);
    }
}
