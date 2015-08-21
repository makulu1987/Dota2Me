package com.makulu.dota2api.model.hero;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xujintian on 2015/8/17.
 */
public class HeroRealm extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String localized_name;


    public HeroRealm() {
    }

    public HeroRealm(String id, String name, String localized_name) {
        this.id = id;
        this.name = name;
        this.localized_name = localized_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String hero_id) {
        this.id = hero_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalized_name() {
        return localized_name;
    }

    public void setLocalized_name(String localized_name) {
        this.localized_name = localized_name;
    }
}