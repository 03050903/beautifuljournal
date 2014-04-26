package com.maple.beautyjournal.entitiy;

import org.json.JSONObject;

/**
 * Created by tian on 13-7-5.
 */
public class Product {
    public String id;
    public String name;
    public String category;
    public String price;
    public String brand;
    public String pic;
    public int star;
    public int comment;
    public int like;
    public String description;
    public boolean favorite;
    public String cat1, cat2, cat3;
    public String functions;
    public int stock;
    public int count = 1;

    public static Product fromJson(JSONObject object) {
        Product p = new Product();
        p.id = object.optString("item_id");
        p.name = object.optString("item_name");
        p.price = object.optString("item_price");
        p.like = object.optInt("item_like");
        p.description = object.optString("item_des");
        p.brand = object.optString("item_brand");
        p.favorite = object.optInt("item_favorite") == 1;
        JSONObject cat = object.optJSONObject("item_cat");
        if (cat != null) {
            p.cat1 = cat.optString("cat1_name");
            p.cat2 = cat.optString("cat2_name");
            p.cat3 = cat.optString("cat3_name");
        }
        p.functions = object.optString("item_functions");
        p.stock = object.optInt("stock_status");
        return p;
    }
}
