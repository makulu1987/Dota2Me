package com.makulu.dota2api.model.hero;

/**
 * Created by xujintian on 2015/8/14.
 */
public class Hero {
    private String id;
    private String name;
    private String localized_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public HeroRealm convertToHeroRealm() {
        return new HeroRealm(id, name, localized_name);
    }

    public Hero(String id, String name, String localized_name) {
        this.id = id;
        this.name = name;
        this.localized_name = localized_name;
    }
}
