package com.makulu.dota2api.model.event;

/**
 * Created by xujintian on 2015/8/18.
 */
public abstract class AbsDota2Event implements Dota2Event{
    protected String message;

    public AbsDota2Event(String message) {
        this.message = message;
    }
}
