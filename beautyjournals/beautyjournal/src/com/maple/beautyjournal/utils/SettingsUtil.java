package com.maple.beautyjournal.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.entitiy.UserInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tian on 13-6-11.
 * 配置辅助类
 */
public class SettingsUtil {
    public static final String PREF_NAME = "preferences";

    public static final String USER_NAME = "username";
    private static final String USER_ID = "user_id";
    private static final String AD_CACHE = "ad_cache";
    private static final String HOTWORDS_CACHE="hotwords_cache";
    private static final String PRODUCTS = "products";
    public static final String GENDER = "gender";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    public static final String ADDRESS = "address";
    private static final String ZIP = "zipcode";
    private static final String IMAGE_NAME = "image_name";
    public static final String ADDRESS_LIST = "address_list";
    public static final String LAST_USER_NAME = "last_username";

    public static final String LAST_NOTIFICATION_TIME_STAMP ="last_notification_time";
    
    //Section UserInfo
    public static synchronized void saveUserInfo(Context context, String username, String id, int gender) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(USER_NAME, username);
        edit.putString(USER_ID, id);
        edit.putInt(GENDER, gender);
        edit.putString(LAST_USER_NAME, username);
        edit.commit();
    }

    public static synchronized UserInfo getUser(Context context){
    	UserInfo user = new UserInfo();
    	user.id = getUserId(context);
    	user.name = getUserName(context);
    	user.email = getEmail(context);

    	// other infos
        SharedPreferences pref = getPrefs(context);    	
    	user.gender = pref.getInt(GENDER, ConstantsHelper.GENDER_FEMALE);
    	user.phone = pref.getString(PHONE, "");
    	user.imagename = pref.getString(IMAGE_NAME, "");
    	user.zipcode = pref.getString(ZIP, "");
    	return user;
    }
    
    public static synchronized void saveUser(Context context, UserInfo user){
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        
        //user info 必须字段
        editor.putString(USER_NAME, user.name);
        editor.putString(USER_ID, user.id);
        editor.putInt(GENDER, user.gender);
        editor.putString(LAST_USER_NAME, user.name);

        //save other infos  其它非必须字段      
        if (!TextUtils.isEmpty(user.phone)) {
            editor.putString(PHONE, user.phone);
        }
        if (!TextUtils.isEmpty(user.email)) {
            editor.putString(EMAIL, user.email);
        }        
        if (!TextUtils.isEmpty(user.imagename)) {
            editor.putString(IMAGE_NAME, user.imagename);
        }        
        if (!TextUtils.isEmpty(user.address)) {
            editor.putString(ADDRESS, user.address);
        }
        if(!TextUtils.isEmpty(user.zipcode)){
            editor.putString(ZIP, user.zipcode);
        }        
        editor.commit();
        String image = user.image;
        if (!TextUtils.isEmpty(image)) {
            NetUtil util = new HttpClientImplUtil(context, URLConstant.SERVER_ADDRESS + image);
            Log.d("ServerDataUtils", "about to download: " + image + " to " + Utils.getAvatarFile(context));
            util.downFile(Utils.getAvatarFile(context).getAbsolutePath());
            Log.d("ServerDataUtils", "downloaded to " + Utils.getAvatarFile(context));
        }
    }

    public static String getEmail(Context context) {
        SharedPreferences pref  = getPrefs(context);
        return pref.getString(EMAIL, "");
    }
    
    public static String getUserName(Context context) {
        SharedPreferences pref  = getPrefs(context);
        return pref.getString(USER_NAME, "");
    }

    public static String getUserId(Context context) {
        SharedPreferences pref  = getPrefs(context);
        return pref.getString(USER_ID, "");
    }

    public static String getLastUserName(Context context){
    	SharedPreferences pref = getPrefs(context);
    	return pref.getString(LAST_USER_NAME, "");
    }
    
    public static synchronized boolean isLoggedIn(Context context) {
        String username = getUserName(context);
        String userId = getUserId(context);
        Log.d(PREF_NAME, "isLoggedIn, username is " + username + ", userId is " + userId);

        boolean ret = !TextUtils.isEmpty(getUserName(context)) && !TextUtils.isEmpty(getUserId(context));
        Log.d(PREF_NAME, "isLoggedIn ret is " + ret);
        return ret;
    }

    public static synchronized void logOut(Context context) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_NAME, "");
        editor.putString(USER_ID, "");
        editor.putString(ADDRESS_LIST, "");
        editor.putString(PHONE, "");
        editor.putString(EMAIL, "");
        editor.putString(ZIP, "");
        editor.putString(PRODUCTS, "");
        editor.commit();
    }

    // Section Products in the Kart
    public static void addProductToKart(Context context, Product product) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String str = pref.getString(PRODUCTS, "");
        JSONArray array;
        try {
            array = new JSONArray(str);
        } catch (JSONException e) {
            array = new JSONArray();
        }
        boolean found = false;
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONArray productData = array.getJSONArray(i);
                Product tmp = convertJSONArrayToProduct(productData);
                if (tmp.id.contentEquals(product.id)) {
                    tmp.count++;
                    found = true;
                    array.put(i, convertProductToJSONArray(tmp));
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!found) {
            array.put(convertProductToJSONArray(product));
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PRODUCTS, array.toString());
        editor.commit();
    }

    private static JSONArray convertProductToJSONArray(Product product) {
        JSONArray array = new JSONArray();
        array.put(product.id);
        array.put(product.name);
        array.put(product.pic);
        array.put(product.price);
        array.put(product.count);
        return array;
    }

    private static Product convertJSONArrayToProduct(JSONArray array) {
        Product product = new Product();
        product.id = array.optString(0);
        product.name = array.optString(1);
        product.pic = array.optString(2);
        product.price = array.optString(3);
        product.count = array.optInt(4);
        return product;
    }

    public static void saveProductList(Context context, List<Product> products) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        JSONArray array = new JSONArray();
        for (Product product : products) {
            array.put(convertProductToJSONArray(product));
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PRODUCTS, array.toString());
        editor.commit();
    }

    public static List<Product> getProductsInKart(Context context) {
        List<Product> productList = new ArrayList<Product>();
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String productsString = pref.getString(PRODUCTS, "");
        if (!TextUtils.isEmpty(productsString)) {
            try {
                JSONArray array = new JSONArray(productsString);
                for (int i = 0; i < array.length(); i++) {
                    JSONArray productArray = array.getJSONArray(i);
                    Product p = convertJSONArrayToProduct(productArray);
                    productList.add(p);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productList;
    }

    // Section AddressList
    public static void saveAddressList(Context context, String content) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ADDRESS_LIST, content);
        editor.commit();
    }

    public static String getAddressList(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(ADDRESS_LIST, null);
    }

    
    // Section AdCache
    public static String getAdCache(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(AD_CACHE, "");
    }
    public static String getHotWordsCache(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(HOTWORDS_CACHE, "");
    }
    public static void saveAdCache(Context context, String cache) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(AD_CACHE, cache);
        editor.commit();
    }

    public static void saveHotWordsCache(Context context,String cache){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(HOTWORDS_CACHE, cache);
        editor.commit();
    }
    // Section sns LoginInfo
    public static void clearQQLogInInfo(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(QQ_DISPLAY_NAME);
        editor.remove(QQ_EXPIRE_TIME);
        editor.remove(QQ_KEY);
        editor.remove(QQ_TOKEN);
        editor.remove(QQ_UID_KEY);
        editor.commit();
    }

    public static void clearSinaLogInInfo(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(SINA_WEIBO_EXPIRE_TIME);
        editor.remove(SINA_WEIBO_TOKEN);
        editor.remove(WEIBO_DISPLAY_NAME);
        editor.remove(WEIBO_UID_KEY);
        editor.commit();
    }
   
    public static void saveWeiboAccessToken(Context context, Oauth2AccessToken accessToken) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SINA_WEIBO_TOKEN, accessToken.getToken());
        editor.putLong(SINA_WEIBO_EXPIRE_TIME, accessToken.getExpiresTime());
        editor.commit();
    }

    public static Oauth2AccessToken readWeiboAccessToken(Context context) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = getPrefs(context);
        token.setToken(pref.getString(SINA_WEIBO_TOKEN, ""));
        token.setExpiresTime(pref.getLong(SINA_WEIBO_EXPIRE_TIME, 0));
        return token;
    }

    public static void saveQQAccessToken(Context context, Oauth2AccessToken accessToken) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(QQ_TOKEN, accessToken.getToken());
        editor.putLong(QQ_EXPIRE_TIME, accessToken.getExpiresTime());
        editor.commit();
    }

    public static Oauth2AccessToken readQQAccessToken(Context context) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = getPrefs(context);
        token.setToken(pref.getString(QQ_TOKEN, ""));
        token.setExpiresTime(pref.getLong(QQ_EXPIRE_TIME, 0));
        return token;
    }

    public static void saveWeiboUid(Context context, String id) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(WEIBO_UID_KEY, id);
        editor.commit();
    }

    public static String getWeiboUid(Context context) {
        return getPrefs(context).getString(WEIBO_UID_KEY, "0");
    }

    public static void saveQQLogInfo(Context context, String info) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(QQ_KEY, info);
        editor.commit();
    }

    public static String getQQLogInfo(Context context) {
        return getPrefs(context).getString(QQ_KEY, "");
    }

    public static String getWeiboName(Context context) {
        return getPrefs(context).getString(WEIBO_DISPLAY_NAME, null);
    }

    public static String getQQName(Context context) {
        return getPrefs(context).getString(QQ_DISPLAY_NAME, null);
    }

    public static void setQQName(Context context, String text) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(QQ_DISPLAY_NAME, text);
        editor.commit();
    }

    public static void saveQQUid(Context context, String id) {
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(QQ_UID_KEY, id);
        editor.commit();
    }

    public static void saveLastNotificationTime(Context context, Long time){
        SharedPreferences pref = getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(LAST_NOTIFICATION_TIME_STAMP, time);
        editor.commit();  	
    }
    
    public static Long getLastNotificationTime(Context context){
    	return getPrefs(context).getLong(LAST_NOTIFICATION_TIME_STAMP, 0);
    }
    
    
    public static String getQQUid(Context context) {
        return getPrefs(context).getString(QQ_UID_KEY, "");
    }

    private static final String WEIBO_UID_KEY = "weibo_uid";
    private static final String QQ_KEY = "qq";
    private static final String WEIBO_DISPLAY_NAME = "weibo_display_name";
    private static final String QQ_DISPLAY_NAME = "qq_display_name";
    private static final String QQ_UID_KEY = "qq_uid";
    private static final String SINA_WEIBO_TOKEN = "sina_weibo_token";
    private static final String SINA_WEIBO_EXPIRE_TIME = "sina_weibo_expire_time";
    private static final String QQ_TOKEN = "qq_token";
    private static final String QQ_EXPIRE_TIME = "qq_expire_time";
    static Context gContext = null;

   //返回配置类SharedPreferences的对象
    @SuppressLint("InlinedApi")
    public static SharedPreferences getPrefs(Context context) {
        if (gContext == null && context != null) {
            gContext = context.getApplicationContext();
        }
        int code = Context.MODE_PRIVATE;     //设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            code = Context.MODE_MULTI_PROCESS;
        }
        return gContext.getSharedPreferences(PREF_NAME, code);
    }

}
