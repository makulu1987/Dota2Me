package com.makulu.dota2api;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xujintian on 2015/8/14.
 */
public class RetrofitUtils {
    private static RestAdapter singleton;

    public static <T> T createApi(Class<T> clazz) {
        if (singleton == null) {
            synchronized (RetrofitUtils.class) {
                if (singleton == null) {
                    RestAdapter.Builder builder = new RestAdapter.Builder();
                    builder.setEndpoint(Dota2Service.Dota2Url);//设置远程地址
                    OkHttpClient client = new OkHttpClient();
                    builder.setClient(new OkClient(client));
                    builder.setLogLevel(RestAdapter.LogLevel.FULL );
                    builder.setErrorHandler(cause -> cause);
                    singleton = builder.build();
                }
            }
        }
        return singleton.create(clazz);
    }

    public static <T> Observable.Transformer<T, T> applyCommon2Main() {
        return observable -> observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable.Transformer<T, T> applyIo2Main() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
