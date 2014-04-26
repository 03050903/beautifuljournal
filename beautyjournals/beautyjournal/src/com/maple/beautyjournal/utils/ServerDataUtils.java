package com.maple.beautyjournal.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.i2mobi.utils.JsonUtility;
import com.maple.beautyjournal.accessor.AccessorResultWrapper;
import com.maple.beautyjournal.entitiy.Article;
import com.maple.beautyjournal.entitiy.Comment;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.entitiy.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tian on 13-5-29.
 */
public class ServerDataUtils {
	/**
	 * @param jsonString
	 * jsonString 的结构如下
	 * {
	 *     "status":"ok/error",
	 *     "info":{}
	 * }
	 * @return
	 * got some hack here
	 */
	public static AccessorResultWrapper parseToJson(String jsonString){
        AccessorResultWrapper result = new AccessorResultWrapper();
        try{
        	JSONObject jsonObject = new JSONObject(jsonString);
            if (ServerDataUtils.isTaskSuccess(jsonObject)) {
            	result.isSuccess = true;
            	//String
            	result.resultMsg = jsonObject.optString("info");
            	//jsonObject
            	result.result = jsonObject.optJSONObject("info");
            	if(result.result == null){
            		//jsonArray
            		JSONArray array = jsonObject.optJSONArray("info");
            		if(array !=null){
            		    result.result = jsonObject.optJSONArray("info").getJSONObject(0);
            		}
            	}
            }else{
            	result.isSuccess = false;
            	result.errorMsg = jsonObject.getString("info");
            }
        }catch(Exception e){
        	result.isSuccess = false;
        	result.errorMsg = e.getLocalizedMessage();
        }
        return result;
	}
	
    public static boolean isTaskSuccess(JSONObject obj) {
        String status = obj.optString("status");
        return status != null && status.equalsIgnoreCase("ok");
    }

    /*
     * do some hack here
     * as there are two possible results
     */
    public static String getErrorMessage(JSONObject obj) {
        String errorMsg = obj.optString("error");
        if(!TextUtils.isEmpty(errorMsg)){
        	return errorMsg;
        }else{
        	return obj.optString("info");
        }
    }

    public static Product getProductFromJSONObject(JSONObject obj) throws JSONException {
        Product product = new Product();
        product.id = obj.getString("item_id");
        product.name = obj.getString("item_name");
        product.category = obj.getString("item_cat");
        product.price = obj.getString("item_price");
        product.brand = obj.getString("item_brand");
        product.pic = obj.optString("item_image");
        product.star = obj.optInt("star");
        product.comment = obj.optInt("comment_count");
        if (TextUtils.isEmpty(product.pic)) { product.pic = obj.optString("item_image"); }
        return product;
    }

    public static Article getArticleFromJSONObject(JSONObject obj) throws JSONException {
        Article article = new Article();
        article.id = JsonUtility.optString(obj,"item_id");
        article.type = obj.getInt("item_type");
        article.category = obj.getString("item_category");
        article.summary = obj.optString("item_summary");
        article.title = obj.getString("item_title");
        article.top = obj.optInt("top");
        article.pic = obj.optString("main_pic");
        article.releaseTime = obj.optLong("release_time");
        return article;
    }

    public static Comment getCommentFromJSONObject(JSONObject obj) throws JSONException {
        Comment comment = new Comment();
        comment.username = obj.getString("username");
        comment.image = URLConstant.SERVER_ADDRESS + obj.getString("userimage");
        comment.productId = obj.getString("product_id");
        comment.productName = obj.getString("product_name");
        comment.content = obj.getString("content");
        comment.star = obj.getInt("star");
        comment.time = obj.getLong("time") * 1000;
        return comment;
    }

    public static UserInfo getUserInfoFromJSONObject(JSONObject obj) throws JSONException{
    	UserInfo user = new UserInfo();
    	user.name = obj.getString("name");
    	user.id = obj.getString("id");
        user.gender = obj.getInt("gender");
        user.phone = obj.optString("phone");
        user.email = obj.optString("email");
        user.imagename = obj.optString("imagename");
        user.image = obj.optString("image");
        user.address = obj.optString("address");
        user.zipcode = obj.optString("zipcode");
        return user;
    }

    public static void updateUserInfo(Context context, String username, String userId, String password) {

    }

    public static void updateAvatar(Context context, String username, String userId, String password) {
        NetUtil util = new HttpClientImplUtil(context, NetUtil.getUpdateInfoUrl(context));

    }

    private static final String TAG = "ServerDataUtils";

    public static void getAddressListFromServer(Context context) throws JSONException {
        String url = NetUtil.getQueryAddressURL(context);
        NetUtil util = new HttpClientImplUtil(context, url);
        String result = util.doGet();
        Log.d(TAG, "getAddressListFromServer, result is " + result);
        JSONObject json = new JSONObject(result);
        if (isTaskSuccess(json)) {
            JSONArray array = json.optJSONArray("info");
            if (array != null && array.length() > 0) {
                SettingsUtil.saveAddressList(context, array.toString());
            }
        }
    }
}
