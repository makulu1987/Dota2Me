package com.makulu.dota2api;

import android.net.Uri;

/**
 * Created by xujintian on 2015/8/17.
 */
public final class UrlGenerator {
    public static Uri generateHeroImage(String hero,HeroSize heroSize){
        return Uri.parse("http://cdn.dota2.com/apps/dota2/images/heroes/" + hero.replace("npc_dota_hero_", "") + heroSize.getStr());
    }
    public static Uri generateItemImage(String item){
        return Uri.parse("http://cdn.dota2.com/apps/dota2/images/items/"+item.replace("item_","")+"_lg.png");
    }
}
