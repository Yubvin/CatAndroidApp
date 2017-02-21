package com.catandroidapp.models;

/**
 * Created by mac on 20.02.17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Cat {

    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("age")
    private short age;

    @SerializedName("breed")
    private String breed;

    @SerializedName("imgName")
    private String imgName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}