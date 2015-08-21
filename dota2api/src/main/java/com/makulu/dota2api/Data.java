package com.makulu.dota2api;

/**
 * Simple data class, keeps track of when it was created
 * so that it knows when the its gone stale.
 */
public class Data<T> {

    private static final long STALE_MS = 60 * 1000; // Data is stale after 5 seconds

    final String value;

    final long timestamp;

    private T data;

    public Data(String value) {
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isUpToDate() {
        return System.currentTimeMillis() - timestamp < STALE_MS;
    }

    public void setData(T data){
        this.data=data;
    }

    public T getData() {
        return data;
    }
}
