package com.makulu.dota2api.model.event;

import android.text.TextUtils;

/**
 * Created by xujintian on 2015/8/18.
 */
public class BaseEvent extends AbsDota2Event {
    public BaseEvent(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        if (TextUtils.isEmpty(message)) return "";
        return message;
    }
}
