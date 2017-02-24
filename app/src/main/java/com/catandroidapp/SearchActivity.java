package com.catandroidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.catandroidapp.adapter.CardAdapter;
import com.catandroidapp.models.Cat;
import com.catandroidapp.services.CatService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

/**
 * Created by mac on 24.02.17.
 */

public class SearchActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView mRecyclerView;
    private final CardAdapter mCardAdapter = new CardAdapter();
    private Button btnSearch;
    private EditText edtSearch;
    private CatService catService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);

        mCardAdapter.setContext(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCardAdapter);

        spinner = (Spinner) findViewById(R.id.spinner);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        edtSearch = (EditText) findViewById(R.id.edtSearch);

        catService = new CatService();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = edtSearch.getText().toString();

                if(!data.equals("")) {
                    switch ((int) spinner.getSelectedItemId()) {
                        case 0:
                            try {
                                long id = Long.valueOf(data);
                                mCardAdapter.clear();
                                getCatsById(id);
                            }catch (NumberFormatException e){
                                Toast.makeText(SearchActivity.this, "ID must be long", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1:
                            mCardAdapter.clear();
                            getCatsByName(data);
                            break;
                        case 2:
                            try {
                                short age = Short.valueOf(data);
                                mCardAdapter.clear();
                                getCatsByAge(age);
                            }catch (NumberFormatException e){
                                Toast.makeText(SearchActivity.this, "Age must be short", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 3:
                            mCardAdapter.clear();
                            getCatsByBreed(data);
                            break;
                    }
                }
                else Toast.makeText(SearchActivity.this, "Please fill search field", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCatsById(long id){

        catService.getCatById(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Cat>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Cat cat) {
                    File file = new File("/data/data/" + getPackageName() + "/cache/" + cat.getImgName());
                    if(!file.exists())
                        catService.getCatImgURL(cat.getImgName())
                                .flatMap(new Function<Response<ResponseBody>, ObservableSource<File>>() {
                                    @Override
                                    public ObservableSource<File> apply(final Response<ResponseBody> resp) throws Exception {

                                        return Observable.create(new ObservableOnSubscribe<File>() {
                                            @Override
                                            public void subscribe(ObservableEmitter<File> e) throws Exception {

                                                try {
                                                    String header = resp.headers().get("Content-Disposition");
                                                    String filename = header.replace("attachment; filename=", "").replace("\"", "");

                                                    new File("/data/data/" + getPackageName() + "/cache").mkdir();

                                                    File pathImgCache = new File("/data/data/" + getPackageName() + "/cache/" + filename);

                                                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(pathImgCache));
                                                    bufferedSink.writeAll(resp.body().source());
                                                    bufferedSink.close();

                                                    e.onNext(pathImgCache);
                                                    e.onComplete();
                                                } catch (IOException er) {
                                                    er.printStackTrace();
                                                    e.onError(er);
                                                }
                                            }
                                        });
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<File>() {
                                    @Override
                                    public void onComplete() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                    }

                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(File file) {
                                    }
                                });

                    mCardAdapter.addCat(cat);

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                }
            });
    }

    public void getCatsByName(String name){
        catService.getCatsByName(name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Cat>, ObservableSource<Cat>>(){
                    @Override
                    public ObservableSource<Cat> apply(List<Cat> cats) throws Exception {
                        return Observable.fromIterable(cats);
                    }
                }).subscribe(new Observer<Cat>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Cat cat) {
                File file = new File("/data/data/" + getPackageName() + "/cache/" + cat.getImgName());
                if(!file.exists())
                    catService.getCatImgURL(cat.getImgName())
                            .flatMap(new Function<Response<ResponseBody>, ObservableSource<File>>() {
                                @Override
                                public ObservableSource<File> apply(final Response<ResponseBody> resp) throws Exception {

                                    return Observable.create(new ObservableOnSubscribe<File>() {
                                        @Override
                                        public void subscribe(ObservableEmitter<File> e) throws Exception {

                                            try {
                                                String header = resp.headers().get("Content-Disposition");
                                                String filename = header.replace("attachment; filename=", "").replace("\"", "");

                                                new File("/data/data/" + getPackageName() + "/cache").mkdir();

                                                File pathImgCache = new File("/data/data/" + getPackageName() + "/cache/" + filename);

                                                BufferedSink bufferedSink = Okio.buffer(Okio.sink(pathImgCache));
                                                bufferedSink.writeAll(resp.body().source());
                                                bufferedSink.close();

                                                e.onNext(pathImgCache);
                                                e.onComplete();
                                            } catch (IOException er) {
                                                er.printStackTrace();
                                                e.onError(er);
                                            }
                                        }
                                    });
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<File>() {
                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(File file) {
                                }
                            });

                mCardAdapter.addCat(cat);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void getCatsByAge(short age){
        catService.getCatsByAge(age)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Cat>, ObservableSource<Cat>>(){
                    @Override
                    public ObservableSource<Cat> apply(List<Cat> cats) throws Exception {
                        return Observable.fromIterable(cats);
                    }
                }).subscribe(new Observer<Cat>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Cat cat) {
                File file = new File("/data/data/" + getPackageName() + "/cache/" + cat.getImgName());
                if(!file.exists())
                    catService.getCatImgURL(cat.getImgName())
                            .flatMap(new Function<Response<ResponseBody>, ObservableSource<File>>() {
                                @Override
                                public ObservableSource<File> apply(final Response<ResponseBody> resp) throws Exception {

                                    return Observable.create(new ObservableOnSubscribe<File>() {
                                        @Override
                                        public void subscribe(ObservableEmitter<File> e) throws Exception {

                                            try {
                                                String header = resp.headers().get("Content-Disposition");
                                                String filename = header.replace("attachment; filename=", "").replace("\"", "");

                                                new File("/data/data/" + getPackageName() + "/cache").mkdir();

                                                File pathImgCache = new File("/data/data/" + getPackageName() + "/cache/" + filename);

                                                BufferedSink bufferedSink = Okio.buffer(Okio.sink(pathImgCache));
                                                bufferedSink.writeAll(resp.body().source());
                                                bufferedSink.close();

                                                e.onNext(pathImgCache);
                                                e.onComplete();
                                            } catch (IOException er) {
                                                er.printStackTrace();
                                                e.onError(er);
                                            }
                                        }
                                    });
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<File>() {
                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(File file) {
                                }
                            });

                mCardAdapter.addCat(cat);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }

    public void getCatsByBreed(String breed){
        catService.getCatsByBreed(breed)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Cat>, ObservableSource<Cat>>(){
                    @Override
                    public ObservableSource<Cat> apply(List<Cat> cats) throws Exception {
                        return Observable.fromIterable(cats);
                    }
                }).subscribe(new Observer<Cat>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Cat cat) {
                File file = new File("/data/data/" + getPackageName() + "/cache/" + cat.getImgName());
                if(!file.exists())
                    catService.getCatImgURL(cat.getImgName())
                            .flatMap(new Function<Response<ResponseBody>, ObservableSource<File>>() {
                                @Override
                                public ObservableSource<File> apply(final Response<ResponseBody> resp) throws Exception {

                                    return Observable.create(new ObservableOnSubscribe<File>() {
                                        @Override
                                        public void subscribe(ObservableEmitter<File> e) throws Exception {

                                            try {
                                                String header = resp.headers().get("Content-Disposition");
                                                String filename = header.replace("attachment; filename=", "").replace("\"", "");

                                                new File("/data/data/" + getPackageName() + "/cache").mkdir();

                                                File pathImgCache = new File("/data/data/" + getPackageName() + "/cache/" + filename);

                                                BufferedSink bufferedSink = Okio.buffer(Okio.sink(pathImgCache));
                                                bufferedSink.writeAll(resp.body().source());
                                                bufferedSink.close();

                                                e.onNext(pathImgCache);
                                                e.onComplete();
                                            } catch (IOException er) {
                                                er.printStackTrace();
                                                e.onError(er);
                                            }
                                        }
                                    });
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<File>() {
                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                }

                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(File file) {
                                }
                            });

                mCardAdapter.addCat(cat);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
            }
        });
    }
}
