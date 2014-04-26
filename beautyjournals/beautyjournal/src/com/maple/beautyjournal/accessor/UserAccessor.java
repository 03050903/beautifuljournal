package com.maple.beautyjournal.accessor;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.i2mobi.utils.DESEncryption;
import com.maple.beautyjournal.entitiy.UserInfo;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.utils.Utils;

public class UserAccessor {
	
	public static AccessorResultWrapper doValidateUserUnique(Context context, String name){
        String validateUserUrl = NetUtil
                .getUserIsUniqueUrl(context, name);
        NetUtil validateUserUtil = new HttpClientImplUtil(context, validateUserUrl);
        String validateUserResult = validateUserUtil.doGet();
        
        AccessorResultWrapper result = new AccessorResultWrapper();
        try{
        	JSONObject validateJson = new JSONObject(validateUserResult);
            if (ServerDataUtils.isTaskSuccess(validateJson)) {
            	result.isSuccess = true;
            	result.result = validateJson;
            }else{
            	result.isSuccess = false;
            	//TODO(shuyinghuang) tocheck something is wrong here
            	result.errorMsg = validateJson.getString("info");
            }
        }catch(Exception e){
        	result.isSuccess = false;
        	result.errorMsg = e.getLocalizedMessage();
        }
        return result;
	}
	
	public static AccessorResultWrapper doRegister(Context context, UserInfo user){
    	//register
        String url = NetUtil.getRegisterUrl(context);
        NetUtil util = new HttpClientImplUtil(context, url);
        Map<String, String> registerMap = new HashMap<String, String>();
        registerMap.put("username", user.name);
        registerMap.put("passwd", DESEncryption.DESEncrypt(user.passwd));
        registerMap.put("gender", String.valueOf(user.gender));
        registerMap.put("platform", "Android");
        registerMap.put("imagename", ""+user.imagename);
        util.setMap(registerMap);
        String result = util.uploadMultiPart();
        
        AccessorResultWrapper finalresult = new AccessorResultWrapper();
        try {
            JSONObject registerJson = new JSONObject(result);
            if (ServerDataUtils.isTaskSuccess(registerJson)) {
            	finalresult.isSuccess = true;
            	finalresult.result = registerJson.getJSONObject("info");
            } else {
            	finalresult.isSuccess = false;
            	finalresult.errorMsg = ServerDataUtils.getErrorMessage(registerJson);
            }
        } catch (Exception e) {
        	finalresult.isSuccess = false;
        	finalresult.errorMsg = e.getLocalizedMessage();
        }
        return finalresult;
	}
	
	public static AccessorResultWrapper doUpdatePasswdByPhone(Context context, String phoneNumber, String newPasswd){
		String url = URLConstant.UPDATE_USER_PASSWD_URL;
        NetUtil util = new HttpClientImplUtil(context, url);
        Map<String, String> map = new HashMap<String, String>();
        map.put("phone", phoneNumber);
        map.put("passwd", DESEncryption.DESEncrypt(newPasswd));
        util.setMap(map);
        String result =util.doPost();
        return ServerDataUtils.parseToJson(result);

	}
	
	public static AccessorResultWrapper doUpdateUser(Context context, UserInfo user){
        String url = NetUtil.getUpdateInfoUrl(context);
        NetUtil util = new HttpClientImplUtil(context, url);
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", SettingsUtil.getUserId(context));
        map.put("username", SettingsUtil.getUserName(context));
        
        if(!TextUtils.isEmpty(user.passwd)){
        	map.put("passwd", DESEncryption.DESEncrypt(user.passwd));
        }
        if (!TextUtils.isEmpty(user.phone)) {
            map.put("phone", user.phone);
        }
        if (!TextUtils.isEmpty(user.email)) {
            map.put("email", user.email);
        }
        if (!TextUtils.isEmpty(user.address)) {
            map.put("address", user.address);
        }
        if (!TextUtils.isEmpty(user.zipcode)) {
            map.put("zipcode", user.zipcode);
        }
        if (!TextUtils.isEmpty(user.zipcode)) {
            map.put("zipcode", user.zipcode);
        }
        map.put("gender", Integer.toString(user.gender));
        util.setMap(map);
        util.setFileEntitiy("image", Utils.getAvatarFile(context));
        String result = util.uploadMultiPart();
        
        AccessorResultWrapper finalresult = new AccessorResultWrapper();
        try {
            JSONObject json = new JSONObject(result);
            if (ServerDataUtils.isTaskSuccess(json)) {
            	finalresult.isSuccess = true;
            }else{
            	finalresult.isSuccess = false;
            	finalresult.errorMsg = json.getString("info");
            }
        }catch(Exception e){
        	finalresult.isSuccess = false;
        	finalresult.errorMsg = e.getLocalizedMessage();        	
        }
        return finalresult;
	}
	
    public static AccessorResultWrapper doLogon(Context context, String userName, String passwd){
    	String desEncryPasswd = DESEncryption.DESEncrypt(passwd);
        String url = NetUtil
                .getLogInUrl(context, userName, desEncryPasswd);
        NetUtil util = new HttpClientImplUtil(context, url);
        Map<String, String> logInMap = new HashMap<String, String>();
        logInMap.put("username", userName);
        logInMap.put("passwd", desEncryPasswd);
        util.setMap(logInMap);
        String result = util.doPost();
        
        AccessorResultWrapper finalresult = new AccessorResultWrapper();
        try {
            JSONObject json = new JSONObject(result);
            if (ServerDataUtils.isTaskSuccess(json)) {
            	finalresult.isSuccess =true;
            	finalresult.result = json;
            }
            else {
            	finalresult.isSuccess = false;
            	finalresult.errorMsg = json.getString("info");
            }
        } catch (Exception e) {
        	finalresult.isSuccess = false;
        	finalresult.errorMsg = e.getLocalizedMessage();
        }
        return finalresult;
    }
    
}
