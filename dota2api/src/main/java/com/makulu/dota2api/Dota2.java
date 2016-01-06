package com.makulu.dota2api;

import android.content.Context;

import com.google.gson.Gson;
import com.makulu.dota2api.model.event.BaseEvent;
import com.makulu.dota2api.model.event.InitFinishEvent;
import com.makulu.dota2api.model.hero.Hero;
import com.makulu.dota2api.model.hero.HeroConverter;
import com.makulu.dota2api.model.hero.HeroRealm;
import com.makulu.dota2api.model.hero.HeroResult;
import com.makulu.dota2api.model.item.Item;
import com.makulu.dota2api.model.item.ItemConverter;
import com.makulu.dota2api.model.item.ItemRealm;
import com.makulu.dota2api.model.item.ItemResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by xingzheng on 2016/1/5.
 */
public final class Dota2 {
    public static void init(Context context) {
        Observable.merge(loadHeros(context), loadItems(context)).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                EventBus.getDefault().post(new InitFinishEvent("初始化完毕!"));
            }

            @Override
            public void onError(Throwable e) {
                EventBus.getDefault().post(new InitFinishEvent("初始化失败!"));
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    private static Observable<List<Hero>> loadHeros(Context context) {
        Observable<List<Hero>> assetsHero = Observable
                .create(new Observable.OnSubscribe<InputStream>() {
                    @Override
                    public void call(Subscriber<? super InputStream> subscriber) {
                        try {
                            subscriber.onNext(context.getAssets().open("heros.json"));
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .flatMap(bufferedReader -> Observable.create(new Observable.OnSubscribe<HeroResult>() {
                    @Override
                    public void call(Subscriber<? super HeroResult> subscriber) {
                        String line;
                        StringBuilder Result = new StringBuilder();
                        try {
                            while ((line = bufferedReader.readLine()) != null)
                                Result.append(line);
                            subscriber.onNext(new Gson().fromJson(Result.toString(), HeroResult.class));
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        } finally {
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }))
                .map(heroResult -> heroResult.result.heroes)
                .compose(saveHerosToDisk(context))
                .subscribeOn(Schedulers.io());

        Observable<List<Hero>> databaseHero = Observable.create(new Observable.OnSubscribe<Hero>() {
            @Override
            public void call(Subscriber<? super Hero> subscriber) {
                Realm realm = Realm.getInstance(context);
                realm.beginTransaction();
                RealmResults<HeroRealm> heroRealms = realm.where(HeroRealm.class).findAll();
                for (HeroRealm heroRealm : heroRealms) {
                    subscriber.onNext(HeroConverter.convertToHero(heroRealm));
                }
                realm.commitTransaction();
                realm.close();
                subscriber.onCompleted();
            }
        }).toList().subscribeOn(Schedulers.io());
        return Observable.concat(databaseHero, assetsHero)
                .takeFirst(heroList -> heroList != null && heroList.size() > 0)
                .compose(logSource("正在加载英雄数据..."))
                .compose(saveHeroToMemory())
                .compose(logSource("英雄数据加载完毕!"));
    }


    private static Observable<List<Item>> loadItems(Context context) {
        Observable<List<Item>> assetsItem = Observable
                .create(new Observable.OnSubscribe<InputStream>() {
                    @Override
                    public void call(Subscriber<? super InputStream> subscriber) {
                        try {
                            subscriber.onNext(context.getAssets().open("items.json"));
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .flatMap(bufReader -> Observable.create(new Observable.OnSubscribe<ItemResult>() {
                    @Override
                    public void call(Subscriber<? super ItemResult> subscriber) {
                        try {
                            String line;
                            StringBuilder Result = new StringBuilder();
                            while ((line = bufReader.readLine()) != null)
                                Result.append(line);
                            subscriber.onNext(new Gson().fromJson(Result.toString(), ItemResult.class));
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        } finally {
                            if (bufReader != null) {
                                try {
                                    bufReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }))
                .map(itemResult -> itemResult.result.items)
                .compose(saveItemsToDisk(context))
                .subscribeOn(Schedulers.io());

        Observable<List<Item>> databaseItems = Observable.create(new Observable.OnSubscribe<Item>() {
            @Override
            public void call(Subscriber<? super Item> subscriber) {
                Realm realm = Realm.getInstance(context);
                realm.beginTransaction();
                RealmResults<ItemRealm> itemRealms = realm.where(ItemRealm.class).findAll();
                for (ItemRealm itemRealm : itemRealms) {
                    subscriber.onNext(ItemConverter.convertToItem(itemRealm));
                }
                realm.commitTransaction();
                realm.close();
                subscriber.onCompleted();
            }
        }).toList().subscribeOn(Schedulers.io());


        return Observable.concat(databaseItems, assetsItem)
                .takeFirst(itemList -> itemList != null && itemList.size() > 0)
                .compose(logSource("正在加载物品数据..."))
                .compose(saveItemToMemory())
                .compose(logSource("物品数据加载完毕!"));
    }

    private static Observable.Transformer<List<Hero>, List<Hero>> saveHeroToMemory() {
        return dataObservable -> dataObservable.doOnNext(Dota2MemoryCache::initHero);
    }

    private static Observable.Transformer<List<Item>, List<Item>> saveItemToMemory() {
        return dataObservable -> dataObservable.doOnNext(Dota2MemoryCache::initItem);
    }


    private static Observable.Transformer<List<Hero>, List<Hero>> saveHerosToDisk(final Context context) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            Realm realm = Realm.getInstance(context);
            realm.beginTransaction();
            for (Hero hero : data) {
                realm.copyToRealmOrUpdate(HeroConverter.convertToHeroRealm(hero));
            }
            realm.commitTransaction();
            realm.close();
        });
    }

    private static Observable.Transformer<List<Item>, List<Item>> saveItemsToDisk(final Context context) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            Realm realm = Realm.getInstance(context);
            realm.beginTransaction();
            for (Item item : data) {
                realm.copyToRealmOrUpdate(ItemConverter.convertToItemRealm(item));
            }
            realm.commitTransaction();
            realm.close();
        });
    }

    private static <T> Observable.Transformer<T, T> logSource(final String source) {
        return dataObservable -> dataObservable.doOnNext(data -> EventBus.getDefault().post(new BaseEvent(source + "")));
    }


    public static Hero getHero(String heroId) {
        return Dota2MemoryCache.getHero(heroId);
    }

    public static Item getItem(String itemId) {
        return Dota2MemoryCache.getItem(itemId);
    }

    public static List<Hero> getHeros(){
        return Dota2MemoryCache.getHeros();
    }
    public static List<Item> getItems(){
        return Dota2MemoryCache.getItems();
    }

    public static void clearMemory(){
        Dota2MemoryCache.clear();
    }
}
