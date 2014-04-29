package com.maple.beautyjournal.entitiy;

import org.json.JSONObject;

/**
 * Created by mosl on 14-4-29.
 * 搜索文章的实体类
 */
public class SearchArticleInfo {

    public String item_id;
    public String item_category;
    public String item_tittle;
    public String item_summary;

    public static SearchArticleInfo fromJson(JSONObject obj){
        SearchArticleInfo articleInfo=new SearchArticleInfo();
        articleInfo.item_id=obj.optString("item_id");
        articleInfo.item_category=obj.optString("item_category");
        articleInfo.item_tittle=obj.optString("item_title");
        articleInfo.item_summary=obj.optString("item_summary");
        return articleInfo;
    }

}
