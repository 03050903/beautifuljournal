package com.maple.beautyjournal.entitiy;

import com.i2mobi.net.URLConstant;

import org.json.JSONObject;

/**
 * Created by tian on 13-7-26.
 */
public class Recommend {
    public String id;
    public String title;
    public String summary;
    public long time;
    public String pic;
    public int type;

    public static final int TYPE_ARTICLE = 0;
    public static final int TYPE_PRODUCT = 1;

    public static Recommend fromJson(JSONObject obj) {
        Recommend rec = new Recommend();
        rec.id = obj.optString("item_id");
        rec.type = obj.optInt("item_type");
        rec.title = obj.optString("item_title");
        rec.summary = obj.optString("item_summary");
        rec.time = obj.optLong("release_time");
        rec.pic = URLConstant.SERVER_ADDRESS + obj.optString("rec_pic");
        return rec;
    }
}
