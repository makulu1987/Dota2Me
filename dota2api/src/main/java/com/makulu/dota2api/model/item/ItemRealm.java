package com.makulu.dota2api.model.item;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xujintian on 2015/8/14.
 */
public class ItemRealm extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String cost;
    private String secret_shop;
    private String side_shop;
    private String recipe;
    private String localized_name;

    public ItemRealm() {
    }

    public ItemRealm(String id, String name, String cost, String secret_shop, String side_shop, String recipe, String localized_name) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.secret_shop = secret_shop;
        this.side_shop = side_shop;
        this.recipe = recipe;
        this.localized_name = localized_name;
    }

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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSecret_shop() {
        return secret_shop;
    }

    public void setSecret_shop(String secret_shop) {
        this.secret_shop = secret_shop;
    }

    public String getSide_shop() {
        return side_shop;
    }

    public void setSide_shop(String side_shop) {
        this.side_shop = side_shop;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getLocalized_name() {
        return localized_name;
    }

    public void setLocalized_name(String localized_name) {
        this.localized_name = localized_name;
    }
}
