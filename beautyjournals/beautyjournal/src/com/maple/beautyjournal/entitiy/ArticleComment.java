package com.maple.beautyjournal.entitiy;

import org.json.JSONObject;

/**
 * Created by mosl on 14-5-2.
 */
public class ArticleComment {

    public static int count;
    public static int offset;
    public String username;
    public String userimage;
    public String content;
    public String star;
    public String time;

    public static ArticleComment fromJson(JSONObject obj){
        ArticleComment articleComment=new ArticleComment();
        articleComment.username=obj.optString("username");
        articleComment.userimage=obj.optString("userimage");
        articleComment.content=obj.optString("content");
        articleComment.star=obj.optString("star");
        articleComment.time=obj.optString("time");
        return articleComment;
    }


}
