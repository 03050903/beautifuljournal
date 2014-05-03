package com.maple.beautyjournal.entitiy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enter on 14-5-2.
 * 分类实体
 */
public class Category {
    public String name;
    public String functionId;
    public List<SubCategory> subs = new ArrayList<SubCategory>();
    public static class SubCategory {
        public long id;
        public String subId;
        public String name;
        public boolean enabled = true; //是否有效
        public int type; //属于分类或者品牌
    }
}
