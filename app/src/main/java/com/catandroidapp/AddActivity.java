package com.catandroidapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.catandroidapp.models.Cat;
import com.catandroidapp.services.CatService;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by mac on 22.02.17.
 */

public class AddActivity extends AppCompatActivity {
    private ImageButton imgBtn;
    private Uri selectedImage = null;
    private ProgressDialog pB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final Button btn_add = (Button) findViewById(R.id.button_add);
        final EditText edt_name = (EditText) findViewById(R.id.editText_name);
        final EditText edt_age = (EditText) findViewById(R.id.editText_age);
        final EditText edt_breed = (EditText) findViewById(R.id.editText_breed);
        imgBtn = (ImageButton) findViewById(R.id.imgButton);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pB = new ProgressDialog(v.getContext());
                pB.setCancelable(false);
                pB.setMessage("Cat downloading ...");
                pB.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pB.setProgress(0);
                pB.setMax(100);
                pB.show();
                String name = edt_name.getText().toString();
                String age = edt_age.getText().toString();
                String breed = edt_breed.getText().toString();

                if(!name.equals("") && !age.equals("") && !breed.equals("")){

                    CatService catService = new CatService();

                    RequestBody rbName = RequestBody.create(MediaType.parse("text/plain"), name);

                    RequestBody rbAge = null;
                    try {
                        short sAge = Short.valueOf(age);
                         rbAge = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sAge));
                    }catch (NumberFormatException e){
                        Toast.makeText(getApplicationContext(), "Age must be short", Toast.LENGTH_LONG).show();
                        pB.dismiss();
                    }

                    RequestBody rbBreed = RequestBody.create(MediaType.parse("text/plain"), breed);

                    MultipartBody.Part body = null;

                    if(selectedImage != null){
                        File f = new File(getPath(selectedImage));
                        RequestBody rbImg = RequestBody.create(MediaType.parse("multipart/form-data"), f);
                        body = MultipartBody.Part.createFormData("image", f.getName(), rbImg);
                    }

                    catService.addCat(rbName, rbAge, rbBreed, body)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Cat>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Cat value) {
                                    Log.d("Cat", value.getImgName());
                                    pB.dismiss();
                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });

                }else{
                    Toast.makeText(AddActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                }

            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 0);

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {

            case 0:
                if(resultCode == RESULT_OK){
                    selectedImage = imageReturnedIntent.getData();
                    imgBtn.setImageURI(selectedImage);
                }
                break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
