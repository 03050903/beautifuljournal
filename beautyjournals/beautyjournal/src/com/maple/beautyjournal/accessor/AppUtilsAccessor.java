package com.maple.beautyjournal.accessor;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;

public class AppUtilsAccessor {

	public static AccessorResultWrapper getPushMessage(Context context){
        if (ConstantsHelper.TEST) {
        	AccessorResultWrapper result = new AccessorResultWrapper();

    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	result.isSuccess = true;
        	JSONObject jsonObject = new JSONObject();   
            try {
            	if(System.currentTimeMillis()%2==0){
					jsonObject.put("id","1");
		            jsonObject.put("article_id", "9510");   
		            jsonObject.put("product_id", "");   
		            jsonObject.put("content", "测试Push-文章-消息");   
		            jsonObject.put("timestamp", "");
            	}else{
					jsonObject.put("id","1");
		            jsonObject.put("article_id", "");   
		            jsonObject.put("product_id", "1177761");   
		            jsonObject.put("content", "测试Push-产品-消息");   
		            jsonObject.put("timestamp", "");            		
            	}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
            result.result = jsonObject;   
        	return result;
        }else{
			String pushMessageUrl = String.format(URLConstant.NOTIFICATION_CHECK_URL);
	        NetUtil pushMsgUtil = new HttpClientImplUtil(context, pushMessageUrl);
	        String pushMsgResult = pushMsgUtil.doGet();
			return ServerDataUtils.parseToJson(pushMsgResult);
        }
	}
	
	public static AccessorResultWrapper getLatestVerstion(Context context){
        if (ConstantsHelper.TEST) {
        	AccessorResultWrapper result = new AccessorResultWrapper();

    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	result.isSuccess = true;
        	JSONObject jsonObject = new JSONObject();   
            try {
				jsonObject.put("version_num","1.2");
	            jsonObject.put("version_changelog", "增加了xxx功能点");   
	            jsonObject.put("download_link", "http://mrjapp.net/webportal/beautyjournal.apk");   
	            jsonObject.put("force_update", "true");
	            jsonObject.put("force_release", "true");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
            result.result = jsonObject;   
        	return result;
        }else{
			String pushMessageUrl = String.format(URLConstant.VERSION_CHECK_URL);
	        NetUtil pushMsgUtil = new HttpClientImplUtil(context, pushMessageUrl);
	        String pushMsgResult = pushMsgUtil.doGet();        
			return ServerDataUtils.parseToJson(pushMsgResult);
        }		
	}
}
