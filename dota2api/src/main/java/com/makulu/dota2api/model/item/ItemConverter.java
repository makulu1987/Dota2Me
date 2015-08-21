package com.makulu.dota2api.model.item;

/**
 * Created by xujintian on 2015/8/17.
 */
public class ItemConverter {

    public static Item convertToItem(ItemRealm itemRealm) {
        return new Item(itemRealm.getId(), itemRealm.getName(), itemRealm.getCost(), itemRealm.getSecret_shop(), itemRealm.getSide_shop(), itemRealm.getRecipe(), itemRealm.getLocalized_name());
    }

    public static ItemRealm convertToItemRealm(Item item) {
        return new ItemRealm(item.getId(), item.getName(), item.getCost(), item.getSecret_shop(), item.getSide_shop(), item.getRecipe(), item.getLocalized_name());
    }
}
