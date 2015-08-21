package com.makulu.dota2api.model.hero;

/**
 * Created by xujintian on 2015/8/17.
 */
public class HeroConverter {
    public static Hero convertToHero(HeroRealm heroRealm){
        return new Hero(heroRealm.getId(),heroRealm.getName(),heroRealm.getLocalized_name());
    }
    public static HeroRealm convertToHeroRealm(Hero hero){
        return new HeroRealm(hero.getId(),hero.getName(),hero.getLocalized_name());
    }
}
