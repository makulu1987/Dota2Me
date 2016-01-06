package com.makulu.dota2api;

import android.content.Context;

import com.google.gson.Gson;
import com.makulu.dota2api.model.event.BaseEvent;
import com.makulu.dota2api.model.event.HeroEvent;
import com.makulu.dota2api.model.event.InitFinishEvent;
import com.makulu.dota2api.model.event.ItemEvent;
import com.makulu.dota2api.model.hero.Hero;
import com.makulu.dota2api.model.hero.HeroConverter;
import com.makulu.dota2api.model.hero.HeroResult;
import com.makulu.dota2api.model.hero.HeroSource;
import com.makulu.dota2api.model.item.Item;
import com.makulu.dota2api.model.item.ItemConverter;
import com.makulu.dota2api.model.item.ItemResult;
import com.makulu.dota2api.model.item.ItemSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by xujintian on 2015/8/17.
 */
public final class Dota2ServiceFactory {
    public static final String key = "D939477D960B9DE3486D82D4C904B10A";
    public static final String lang = "zh";
    private static final HeroSource sources = new HeroSource();

    public static void clearHeroMemory() {
        sources.clearMemory();
    }

    public static void init(Context context) {
        Observable<Hero> heros = Observable
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
                .compose(logSource("正在解压英雄数据..."))
                .doOnNext(heroResult -> Dota2MemoryCache.initHero(heroResult.result.heroes))
                .flatMap(heroResult -> Observable.from(heroResult.result.heroes))
                .compose(saveHeroToDisk(context))
                .compose(heroObservable -> heroObservable.doOnNext(hero -> EventBus.getDefault().post(new HeroEvent(hero.getLocalized_name() + "保存完毕!"))))
                .subscribeOn(Schedulers.io());


        Observable<Item> items = Observable
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
                .compose(logSource("正在解压物品数据..."))
                .doOnNext(itemResult -> Dota2MemoryCache.initItem(itemResult.result.items))
                .flatMap(itemResult -> Observable.from(itemResult.result.items))
                .compose(saveItemToDisk(context))
                .compose(itemObservable -> itemObservable.doOnNext(item -> EventBus.getDefault().post(new ItemEvent(item.getLocalized_name() + "保存完毕!"))))
                .subscribeOn(Schedulers.io());
        Observable.merge(heros, items).subscribe(new Subscriber<Object>() {
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

    public static Observable<Data<List<Hero>>> getHeros(Context context) {

        // Create our sequence for querying best available data
        return Observable.concat(
                sources.memory(),
                sources.disk(context),
                sources.network(context, key, lang)
        ).first(data -> data != null
                && data.isUpToDate()
                && data.getData() != null
                && data.getData().size() > 0)
                .compose(RetrofitUtils.applyCommon2Main());
    }

    public static Observable<Data<List<Item>>> getGameItems(Context context) {
        ItemSource sources = new ItemSource();
        // Create our sequence for querying best available data
        return Observable.concat(
                sources.memory(),
                sources.disk(context),
                sources.network(context, key, lang)
        ).first(data -> data != null && data.isUpToDate() && data.getData() != null && data.getData().size() > 0)
                .compose(RetrofitUtils.applyCommon2Main());
    }

    private static Observable.Transformer<Hero, Hero> saveHeroToDisk(final Context context) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            Realm realm = Realm.getInstance(context);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(HeroConverter.convertToHeroRealm(data));
            realm.commitTransaction();
            realm.close();
            EventBus.getDefault().post(new HeroEvent("正在保存" + data.getLocalized_name()));
        });
    }

    private static Observable.Transformer<Item, Item> saveItemToDisk(final Context context) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            Realm realm = Realm.getInstance(context);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(ItemConverter.convertToItemRealm(data));
            realm.commitTransaction();
            realm.close();
            EventBus.getDefault().post(new ItemEvent("正在保存" + data.getLocalized_name()));
        });
    }

    private static <T> Observable.Transformer<T, T> logSource(final String source) {
        return dataObservable -> dataObservable.doOnNext(data -> EventBus.getDefault().post(new BaseEvent(source + "")));
    }
}
