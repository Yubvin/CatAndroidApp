package com.catandroidapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

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


public class MainActivity extends AppCompatActivity {

    private CatService catService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final CardAdapter mCardAdapter = new CardAdapter();
        mRecyclerView.setAdapter(mCardAdapter);

        catService = new CatService();

        catService.getCats()
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
