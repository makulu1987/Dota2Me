package com.makulu.dota2api.model.item;

import android.content.Context;

import com.makulu.dota2api.Data;
import com.makulu.dota2api.Dota2Service;
import com.makulu.dota2api.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by xujintian on 2015/8/14.
 */
public class ItemSource {

    // Memory cache of data
    private Data<List<Item>> memory = null;
//    private static Data<List<Item>> disk = null;


    // Each "network" response is different
    private int requestNumber = 0;

    // In order to simulate memory being cleared, but data still on disk
    public void clearMemory() {
        System.out.println("Wiping memory...");
        memory = null;
    }

    public Observable<Data<List<Item>>> memory() {
        Observable<Data<List<Item>>> observable = Observable.create(subscriber -> {
            subscriber.onNext(memory);
            subscriber.onCompleted();
        });

        return observable.compose(logSource("MEMORY"));
    }

    public Observable<Data<List<Item>>> disk(final Context context) {
        Observable<Data<List<Item>>> observable = Observable.create(subscriber -> {


            Realm instance = Realm.getInstance(context);
            RealmQuery<ItemRealm> query = instance.where(ItemRealm.class);
            RealmResults<ItemRealm> result1 = query.findAll();
            List<Item> resultCopy = new ArrayList<>();
            for (ItemRealm item : result1) {
                resultCopy.add(ItemConverter.convertToItem(item));
            }
            instance.close();
            Data<List<Item>> data = new Data<>("1000");
            data.setData(resultCopy);
            subscriber.onNext(data);
            subscriber.onCompleted();

        });
        observable.observeOn(Schedulers.io());

        // Cache disk responses in memory
        return observable.compose(saveToMemory())
                .compose(logSource("DISK"));
    }

    public Observable<Data<List<Item>>> network(final Context context,final String key, final String lang) {
        Observable<Data<List<Item>>> observable = RetrofitUtils.createApi(Dota2Service.class)
                .getGameItems(key, lang)
                .flatMap(result -> {
                    requestNumber++;
                    Data<List<Item>> data = new Data<>("Server Response #" + requestNumber);
                    data.setData(result.result.items);
                    return Observable.just(data);
                });

        // Save network responses to disk and cache in memory
        return observable
                .compose(saveToMemory())
                .compose(saveToDisk(context))
                .compose(logSource("NETWORK"));
    }

    // Simple logging to let us know what each source is returning
    Observable.Transformer<Data<List<Item>>, Data<List<Item>>> logSource(final String source) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            if (data == null) {
                System.out.println(source + " does not have any data.");
            } else if (!data.isUpToDate()) {
                System.out.println(source + " has stale data.");
            } else {
                System.out.println(source + " has the data you are looking for!");
            }
        });
    }

    Observable.Transformer<Data<List<Item>>, Data<List<Item>>> saveToMemory() {
        return dataObservable -> dataObservable.doOnNext(data -> {
            memory = data;
            System.out.println("data is saved to memory.");
        });
    }

    Observable.Transformer<Data<List<Item>>, Data<List<Item>>> saveToDisk(final Context context) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            List<Item> items = data.getData();
            Realm realm = Realm.getInstance(context);
            realm.beginTransaction();
            for (Item item : items) {
                realm.copyToRealmOrUpdate(ItemConverter.convertToItemRealm(item));
            }
            realm.commitTransaction();
            realm.close();
            System.out.println("data is saved to disk.");
        });
    }
}
