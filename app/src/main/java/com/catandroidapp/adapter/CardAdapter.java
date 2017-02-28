package com.catandroidapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.catandroidapp.MainActivity;
import com.catandroidapp.R;
import com.catandroidapp.models.Cat;
import com.catandroidapp.services.CatService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mac on 21.02.17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Cat> mCats;
    private CatService catService;
    private Context context;

    public CardAdapter() {
        super();
        mCats = new ArrayList<Cat>();
        catService = new CatService();
    }

    public void setContext(Context context){
        this.context = context;
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
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final Cat cat = mCats.get(i);
        viewHolder.id.setText("Id: " + String.valueOf(cat.getId()));
        viewHolder.name.setText("Name: " + cat.getName());
        viewHolder.age.setText("Age: " + String.valueOf(cat.getAge()));
        viewHolder.breed.setText("Breed: " + cat.getBreed());

        File img = new File("/data/data/com.catandroidapp/cache/" + cat.getImgName());
        viewHolder.imgCat.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ID: ", String.valueOf(cat.getId()));
                catService.deleteCat(cat.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Cat>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Cat cat) {
                                mCats.remove(i);
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i,mCats.size());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("Cat", e.toString());
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
        });

        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHolder.editName.setText("");
                viewHolder.editAge.setText("");
                viewHolder.editBreed.setText("");

                viewHolder.editName.setVisibility((viewHolder.editName.getVisibility() == View.VISIBLE)
                        ? View.GONE : View.VISIBLE);

                viewHolder.editAge.setVisibility((viewHolder.editAge.getVisibility() == View.VISIBLE)
                        ? View.GONE : View.VISIBLE);

                viewHolder.editBreed.setVisibility((viewHolder.editBreed.getVisibility() == View.VISIBLE)
                        ? View.GONE : View.VISIBLE);

                viewHolder.btnConfirm.setVisibility((viewHolder.btnConfirm.getVisibility() == View.VISIBLE)
                        ? View.GONE : View.VISIBLE);

                viewHolder.name.setVisibility((viewHolder.editName.getVisibility() == View.VISIBLE)
                        ? View.GONE : View.VISIBLE);

                viewHolder.age.setVisibility((viewHolder.editAge.getVisibility() == View.VISIBLE)
                        ? View.GONE : View.VISIBLE);

                viewHolder.breed.setVisibility((viewHolder.editBreed.getVisibility() == View.VISIBLE)
                        ? View.GONE : View.VISIBLE);
            }
        });

        viewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = viewHolder.editName.getText().toString();
                String age = viewHolder.editAge.getText().toString();
                String breed = viewHolder.editBreed.getText().toString();

                if(!name.equals("") && !age.equals("") && !breed.equals("")){
                    mCats.get(i).setName(name);
                    try {
                        mCats.get(i).setAge(Short.valueOf(age));
                    }catch (NumberFormatException e){
                        Toast.makeText(context, "Age must be short", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mCats.get(i).setBreed(breed);
                    notifyDataSetChanged();
                    catService.updateCat(mCats.get(i))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Cat>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Cat cat) {

                                    viewHolder.editName.setText("");
                                    viewHolder.editAge.setText("");
                                    viewHolder.editBreed.setText("");

                                    viewHolder.editName.setVisibility((viewHolder.editName.getVisibility() == View.VISIBLE)
                                            ? View.GONE : View.VISIBLE);

                                    viewHolder.editAge.setVisibility((viewHolder.editAge.getVisibility() == View.VISIBLE)
                                            ? View.GONE : View.VISIBLE);

                                    viewHolder.editBreed.setVisibility((viewHolder.editBreed.getVisibility() == View.VISIBLE)
                                            ? View.GONE : View.VISIBLE);

                                    viewHolder.btnConfirm.setVisibility((viewHolder.btnConfirm.getVisibility() == View.VISIBLE)
                                            ? View.GONE : View.VISIBLE);

                                    viewHolder.name.setVisibility((viewHolder.editName.getVisibility() == View.VISIBLE)
                                            ? View.GONE : View.VISIBLE);

                                    viewHolder.age.setVisibility((viewHolder.editAge.getVisibility() == View.VISIBLE)
                                            ? View.GONE : View.VISIBLE);

                                    viewHolder.breed.setVisibility((viewHolder.editBreed.getVisibility() == View.VISIBLE)
                                            ? View.GONE : View.VISIBLE);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("Cat", e.toString());
                                }

                                @Override
                                public void onComplete() {
                                }
                            });
                }else{
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        public ImageView imgCat;
        public ImageButton btnDelete;
        public ImageButton btnEdit;
        public EditText editName;
        public EditText editAge;
        public EditText editBreed;
        public Button btnConfirm;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            age = (TextView) itemView.findViewById(R.id.age);
            breed = (TextView) itemView.findViewById(R.id.breed);
            imgCat = (ImageView) itemView.findViewById(R.id.imgCat);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
            btnEdit = (ImageButton) itemView.findViewById(R.id.btnEdit);

            editName = (EditText) itemView.findViewById(R.id.edit_name);
            editAge = (EditText) itemView.findViewById(R.id.edit_age);
            editBreed = (EditText) itemView.findViewById(R.id.edit_breed);
            btnConfirm = (Button) itemView.findViewById(R.id.btnConfirm);
        }
    }
}
