package com.maple.beautyjournal.entitiy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tian on 13-7-8.
 */
public class Function {
    public String name;
    public String functionId;
    public List<SubFunction> subs = new ArrayList<SubFunction>();
    public static class SubFunction {
        public long id;
        public String subId;
        public String name;
    }
}
