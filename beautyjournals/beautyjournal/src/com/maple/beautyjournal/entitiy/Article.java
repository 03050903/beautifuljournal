package com.maple.beautyjournal.entitiy;

import android.text.TextUtils;

/**
 * Created by tian on 13-7-3.
 */
public class Article {
    public String id;
    public int type;
    public String category;
    public String title;
    public String summary;
    public int top;
    public String pic;
    public int groupId;
    public long releaseTime;
    public boolean hasPic() {return !TextUtils.isEmpty(pic);}

}
