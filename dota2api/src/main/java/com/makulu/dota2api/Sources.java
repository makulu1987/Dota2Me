package com.makulu.dota2api;

import rx.Observable;

/**
 * Simulates three different sources - one from memory, one from disk,
 * and one from network. In reality, they're all in-memory, but let's
 * play pretend.
 *
 * Observable.create() is used so that we always return the latest data
 * to the subscriber; if you use just() it will only return the data from
 * a certain point in time.
 */
public class Sources<T> {

    // Memory cache of data
    private Data<T> memory = null;

    // What's currently "written" on disk
    private Data<T> disk = null;

    // Each "network" response is different
    private int requestNumber = 0;

    // In order to simulate memory being cleared, but data still on disk
    public void clearMemory() {
        System.out.println("Wiping memory...");
        memory = null;
    }

    public Observable<Data<T>> memory() {
        Observable<Data<T>> observable = Observable.create(subscriber -> {
            subscriber.onNext(memory);
            subscriber.onCompleted();
        });

        return observable.compose(logSource("MEMORY"));
    }

    public Observable<Data<T>> disk() {
        Observable<Data<T>> observable = Observable.create(subscriber -> {
            subscriber.onNext(disk);
            subscriber.onCompleted();
        });

        // Cache disk responses in memory
        return observable.doOnNext(data -> memory = data)
            .compose(logSource("DISK"));
    }

    public Observable<Data<T>> network() {
        Observable<Data<T>> observable = Observable.create(subscriber -> {
            requestNumber++;
            Data<T> data= new Data<>("Server Response #" + requestNumber);
            subscriber.onNext(data);
            subscriber.onCompleted();
        });

        // Save network responses to disk and cache in memory
        return observable.doOnNext(data -> {
                disk = data;
                memory = data;
            })
            .compose(logSource("NETWORK"));
    }

    // Simple logging to let us know what each source is returning
    Observable.Transformer<Data<T>, Data<T>> logSource(final String source) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            if (data == null) {
                System.out.println(source + " does not have any data.");
            }
            else if (!data.isUpToDate()) {
                System.out.println(source + " has stale data.");
            }
            else {
                System.out.println(source + " has the data you are looking for!");
            }
        });
    }
    Observable.Transformer<Data<T>, Data<T>> saveToDisk(final String source) {
        return dataObservable -> dataObservable.doOnNext(data -> {
            if (data == null) {
                System.out.println(source + " does not have any data.");
            }
            else if (!data.isUpToDate()) {
                System.out.println(source + " has stale data.");
            }
            else {
                System.out.println(source + " has the data you are looking for!");
            }
        });
    }

}
