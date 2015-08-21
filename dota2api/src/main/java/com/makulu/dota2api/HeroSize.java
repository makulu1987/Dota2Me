package com.makulu.dota2api;

/**
 * Created by xujintian on 2015/8/17.
 */
public enum HeroSize {
    /**
     * small horizontal portrait - 59x33px
     */
    SHP("_sb.png", 59, 33),
    /**
     * large horizontal portrait - 205x11px
     */
    LHP("_lg.png", 205, 115),
    /**
     * full quality horizontal portrait - 256x114px
     */
    FQHP("_full.png", 256, 114),
    /**
     * full quality vertical portrait - 234x272px (note this is a .jpg)
     */
    FQVP("_vert.jpg", 234, 272);

    private String str;
    private float width;
    private float height;

    HeroSize(String str, float width, float height) {
        this.str = str;
        this.width = width;
        this.height = height;
    }

    public String getStr() {
        return str;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
