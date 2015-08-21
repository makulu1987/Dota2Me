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
import java.io.InputStreamReader;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
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
        Observable heros=Observable.create(new Observable.OnSubscribe<HeroResult>() {
            @Override
            public void call(Subscriber<? super HeroResult> subscriber) {
                InputStreamReader inputStream = null;
                BufferedReader bufReader = null;
                try {
                    inputStream = new InputStreamReader(context.getAssets().open("heros.json"));
                    bufReader = new BufferedReader(inputStream);
                    String line;
                    StringBuilder Result = new StringBuilder();
                    while ((line = bufReader.readLine()) != null)
                        Result.append(line);
                    subscriber.onNext(new Gson().fromJson(Result.toString(), HeroResult.class));
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
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        })
                .compose(logSource("正在解压英雄数据..."))
                .flatMap(heroResult -> Observable.from(heroResult.result.heroes))
                .compose(saveHeroToDisk(context))
                .subscribeOn(Schedulers.io())
                .doOnNext(hero -> EventBus.getDefault().post(new HeroEvent(hero.getLocalized_name() + "保存完毕!")));
        Observable items=Observable.create(new Observable.OnSubscribe<ItemResult>() {
            @Override
            public void call(Subscriber<? super ItemResult> subscriber) {
                InputStreamReader inputStream = null;
                BufferedReader bufReader = null;
                try {
                    inputStream = new InputStreamReader(context.getAssets().open("items.json"));
                    bufReader = new BufferedReader(inputStream);
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
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        })
                .compose(logSource("正在解压物品数据..."))
                .flatMap(itemResult -> Observable.from(itemResult.result.items))
                .compose(saveItemToDisk(context))
                .subscribeOn(Schedulers.io())
                .doOnNext(item -> EventBus.getDefault().post(new ItemEvent(item.getLocalized_name() + "保存完毕!")));
        Observable.merge(heros,items).subscribe((Action1) o -> {
            EventBus.getDefault().post(new InitFinishEvent("初始化完毕!"));
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
