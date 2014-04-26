package com.maple.beautyjournal.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tian on 13-7-4.
 */
public class Beauty {
    public static final String AUTHORITY = "com.maple.beautyjournal";

    public static final class Brand implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/brand");
        public static final String TABLE_NAME = "brand";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String ENGLISH_NAME = "english_name";
        public static final String FIRST_CHAR = "first_char";
        public static final String FIRST_ENGLISH_CHAR = "first_english_char";
        public static final String BRAND_ID = "brand_id";
        public static final String FAVORITE = "favorite";
        public static final String NOTES = "notes";
        public static final String PINYIN = "pinyin";
        //blob
        public static final String PICTURE = "picture";

        //RFU
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
    }

    public static final class Category implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/category");

        public static final Uri CATEGORY_BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/category_base");
        public static final String TABLE_NAME = "category";
        public static final String NAME = "name";
        public static final String CATEGORY_ID = "category_id";
        public static final String SUB_CATEGORY = "sub_category";
        public static final String SUB_CATEGORY_ID = "sub_category_id";
        public static final String SUB_SUB_CATEGORY = "sub_sub_category";
        public static final String SUB_SUB_CATEGORY_ID = "sub_sub_category_id";
        public static final String NOTES = "notes";
        public static final String FAVORITE = "favorite";
        //RFU
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        //blob
        public static final String PICTURE = "picture";
    }

    public static final class Function implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/function");
        public static final String TABLE_NAME = "function";
        public static final String NAME = "name";
        public static final String FUNCTION_ID = "function_id";
        public static final String SUB_FUNCTION = "sub_function";
        public static final String SUB_FUNCTION_ID = "sub_function_id";
        public static final String NOTES = "notes";
        public static final String FAVORITE = "favorite";
        //RFU
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        //blob
        public static final String PICTURE = "picture";
    }

    public static final class Area implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/area");
        public static final Uri DISTINCT_PROVINCE_CONTENT_URI = Uri
                .parse("content://" + AUTHORITY + "/area-distinct-province");
        public static final Uri DISTINCT_CITY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/area-distinct-city");

        public static final String TABLE_NAME = "area";
        public static final String PROVINCE = "province";
        public static final String CITY = "city";
        public static final String DISTRICT = "district";
        public static final String PAY_AT_ARRIVAL = "pay_arrival";
        public static final String POS = "pos";
    }
}
