package com.catandroidapp.adapter;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.catandroidapp.R;
import com.catandroidapp.models.Cat;
import com.catandroidapp.services.CatService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 21.02.17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Cat> mCats;
    private CatService catService;

    public CardAdapter() {
        super();
        mCats = new ArrayList<Cat>();
        catService = new CatService();
    }

    public void addCat(Cat cat) {
        mCats.add(cat);
        notifyDataSetChanged();
    }

    public void clear() {
        mCats.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Cat cat = mCats.get(i);
        viewHolder.id.setText("Id: " + String.valueOf(cat.getId()));
        viewHolder.name.setText("Name: " + cat.getName());
        viewHolder.age.setText("Age: " + cat.getAge());
        viewHolder.breed.setText("Breed: " + cat.getBreed());
        viewHolder.imgName.setText("Image Name: " + cat.getImgName());
        File img = new File("/data/data/com.catandroidapp/cache/" + cat.getImgName());
        viewHolder.imgCat.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
    }

    @Override
    public int getItemCount() {
        return mCats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView name;
        public TextView age;
        public TextView breed;
        public TextView imgName;
        public ImageView imgCat;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            breed = (TextView) itemView.findViewById(R.id.breed);
            imgName = (TextView) itemView.findViewById(R.id.imgName);
            imgCat = (ImageView) itemView.findViewById(R.id.imgCat);
        }
    }
}
