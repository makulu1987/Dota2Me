package com.makulu.dota2api;

import com.makulu.dota2api.model.hero.Hero;
import com.makulu.dota2api.model.item.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xingzheng on 2015/12/31.
 */
public final class DotaCache {
    private static final Map<String,Hero> heros=new HashMap<>();
    private static final Map<String,Item> items=new HashMap<>();
    public static void initHero(List<Hero> heroList){
        for(Hero hero:heroList){
            heros.put(hero.getId(),hero);
        }
    }
    public static Hero getHero(String heroId){
        return heros.get(heroId);
    }
    public static void initItem(List<Item> itemList){
        for(Item item:itemList){
            items.put(item.getId(),item);
        }
    }
    public static Item getItem(String itemId){
        return items.get(itemId);
    }
}
