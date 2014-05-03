package com.maple.beautyjournal.entitiy;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by mosl on 14-4-29.
 */
public class SearchProductInfo {

    public String item_id;
    public String item_name;
    public String item_price;
    public String item_functions;
    public String item_brand;
    public String item_des;
    public String item_image;

    public static SearchProductInfo fromJson(JSONObject obj){
        SearchProductInfo searchProductInfo=new SearchProductInfo();
        searchProductInfo.item_id=obj.optString("item_id");
        searchProductInfo.item_name=obj.optString("item_name");
        searchProductInfo.item_price=obj.optString("item_price");
        searchProductInfo.item_functions=obj.optString("item_functions");
        searchProductInfo.item_brand=obj.optString("item_brand");
        searchProductInfo.item_des=obj.optString("item_des");
        searchProductInfo.item_image=obj.optString("item_image");
        Log.d("XXX", searchProductInfo.item_image);
        return searchProductInfo;
    }
}
