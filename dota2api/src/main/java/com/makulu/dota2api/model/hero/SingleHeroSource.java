//package com.makulu.dota2api.model.hero;
//
//import com.makulu.dota2me.Data;
//import com.makulu.dota2me.Dota2Service;
//import com.makulu.dota2me.DotaApplication;
//import com.makulu.dota2me.RetrofitUtils;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import io.realm.Realm;
//import io.realm.RealmQuery;
//import rx.Observable;
//import rx.schedulers.Schedulers;
//
///**
// * Created by xujintian on 2015/8/14.
// */
//public class SingleHeroSource {
//
//    // Memory cache of data
//    private Map<String, Data<Hero>> memory = null;
////    private static Data<Hero> disk = null;
//
//
//    // Each "network" response is different
//    private int requestNumber = 0;
//
//    // In order to simulate memory being cleared, but data still on disk
//    public void clearMemory() {
//        System.out.println("Wiping memory...");
//        memory = null;
//    }
//
//    public Observable<Data<Hero>> memory(String id) {
//        Observable<Data<Hero>> observable = Observable.create(subscriber -> {
//            if (memory != null) {
//                subscriber.onNext(memory.get(id));
//            } else {
//                subscriber.onNext(null);
//            }
//            subscriber.onCompleted();
//        });
//
//        return observable.compose(logSource("MEMORY"));
//    }
//
//    public Observable<Data<Hero>> disk(String id) {
//        Observable<Data<Hero>> observable = Observable.create(subscriber -> {
//
//
//            Realm instance = Realm.getInstance(DotaApplication.getContext());
//            RealmQuery<HeroRealm> query = instance.where(HeroRealm.class).equalTo("id", id);
//            HeroRealm result1 = query.findFirst();
//            Hero resultCopy = null;
//            if (result1 != null)
//                resultCopy = new Hero(result1.getId(), result1.getName(), result1.getLocalized_name());
//            instance.close();
//            Data<Hero> data = new Data<>("1000");
//            data.setData(resultCopy);
//            subscriber.onNext(data);
//            subscriber.onCompleted();
//
//        });
//        observable.observeOn(Schedulers.io());
//
//        // Cache disk responses in memory
//        return observable.doOnNext(data -> {
//            if (memory == null)
//                memory = new HashMap<>();
//            memory.put(id, data);
//        }).compose(logSource("DISK"));
//    }
//
//    public Observable<Data<Hero>> network(final String key, final String lang, final String id) {
//        Observable<Data<Hero>> observable = RetrofitUtils.createApi(Dota2Service.class)
//                .getHeros(key, lang)
//                .flatMap(result -> {
//                    requestNumber++;
//                    Data<Hero> data = new Data<>("Server Response #" + requestNumber);
//                    data.setData(result.result.heroes);
//                    return Observable.just(data);
//                });
//
//        // Save network responses to disk and cache in memory
//        return observable
//                .compose(saveToMemory())
//                .compose(saveToDisk())
//                .compose(logSource("NETWORK"));
//    }
//
//    // Simple logging to let us know what each source is returning
//    Observable.Transformer<Data<Hero>, Data<Hero>> logSource(final String source) {
//        return dataObservable -> dataObservable.doOnNext(data -> {
//            if (data == null) {
//                System.out.println(source + " does not have any data.");
//            } else if (!data.isUpToDate()) {
//                System.out.println(source + " has stale data.");
//            } else {
//                System.out.println(source + " has the data you are looking for!");
//            }
//        });
//    }
//
//    Observable.Transformer<Data<Hero>, Data<Hero>> saveToMemory() {
//        return dataObservable -> dataObservable.doOnNext(data -> {
//            memory = data;
//            System.out.println("data is saved to memory.");
//        });
//    }
//
//    Observable.Transformer<Data<Hero>, Data<Hero>> saveToDisk() {
//        return dataObservable -> dataObservable.doOnNext(data -> {
//            Hero heros = data.getData();
//            Realm realm = Realm.getInstance(DotaApplication.getContext());
//            realm.beginTransaction();
//            for (Hero hero : heros) {
//                realm.copyToRealmOrUpdate(HeroConverter.convertToHeroRealm(hero));
//            }
//            realm.commitTransaction();
//            realm.close();
//            System.out.println("data is saved to disk.");
//        });
//    }
//}
