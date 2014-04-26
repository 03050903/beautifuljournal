package com.maple.beautyjournal.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.maple.beautyjournal.entitiy.UserInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.tauth.Constants;
import com.tencent.tauth.Tencent;

public class AuthUtils {

	public static String getAuthRelatedPassword(AuthType authType){
		if(AuthType.QQ.equals(authType)){
			return "asdf1234";
		}else if(AuthType.Sina.equals(authType)){
			return "qwer1234";
		}else
			return "abcd1234";
	}
	
	public static enum AuthType{
		
		Sina(1),
		QQ(2);
		
		int val;
		private AuthType(int v){
			val = v;
		}
	}
	
	public static void parseUserInfoFromQQ(JSONObject json, UserInfo userInfo ){
		userInfo.name = json.optString("nickname");
		String genderString = json.optString("gender");
		userInfo.gender = "男".equals(genderString)?1:0;
		userInfo.imageUrl = json.optString("figureurl_qq_2");
	}
	
	public static UserInfo syncGetQQUserInfo(Tencent tencent){
		try{
			JSONObject json = tencent.request(Constants.GRAPH_SIMPLE_USER_INFO, null,
	                Constants.HTTP_GET);
			UserInfo userInfo = new UserInfo();
			parseUserInfoFromQQ(json,userInfo);
			return userInfo;
		}catch(Exception e){
			Log.e("syncGetQQUserInfo", e.getLocalizedMessage());
			return null;
		}
	}
	
	public static UserInfo parseUserInfoFromSina(String result, UserInfo userInfo){
		try {
			JSONObject json = new JSONObject(result);
			userInfo.name = json.optString("screen_name");
			userInfo.gender = "m".equals(json.optString("gender"))?1:0;
			userInfo.imageUrl = json.optString("profile_image_url");
			return userInfo;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static UserInfo syncGetSinaUserInfo(Oauth2AccessToken accessToken){
		//new UsersAPI (accessToken).show(uid, listener);
		return null;
	}
}
