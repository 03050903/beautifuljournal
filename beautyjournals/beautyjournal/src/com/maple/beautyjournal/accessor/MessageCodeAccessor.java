package com.maple.beautyjournal.accessor;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;

public class MessageCodeAccessor {

	public static enum MsgCodeTypeEnum{
		TypeFindPassword(1),
		TypeRegister(2);

		private int mVal;
		private MsgCodeTypeEnum(int val){
			mVal = val;
		}
		
		public int getVal(){
			return this.mVal;
		}
	}
	
	public static AccessorResultWrapper getMsgCode(Context context, MsgCodeTypeEnum type, String phoneNumber){		
        if (ConstantsHelper.TEST) {
        	AccessorResultWrapper result = new AccessorResultWrapper();
        	result.isSuccess = true;
        	result.result =new JSONObject();
        	try {
        		Thread.sleep(6000);
				result.result.put("info", "100110");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return result;
        }else{
			String msgCodeUrl = String.format(URLConstant.MESSAGECODE_GET_URL,type.getVal(), phoneNumber);
	        NetUtil msgCodeUtil = new HttpClientImplUtil(context, msgCodeUrl);
	        String msgCodeResult = msgCodeUtil.doGet();        
			return ServerDataUtils.parseToJson(msgCodeResult);
        }
	}
	
	public static AccessorResultWrapper verifyMsgCode(Context context, String phoneNumber, String msgCode){
        if (ConstantsHelper.TEST) {
        	AccessorResultWrapper result = new AccessorResultWrapper();

    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	if("100110".equals(msgCode)){
        		result.isSuccess = true;
        	}else{
        		result.isSuccess = false;
        		result.errorMsg="验证码不一致";
        	}
        	return result;
        }else{
			String msgVerifyCodeUrl = String.format(URLConstant.MESSAGECODE_VERIFY_URL, phoneNumber, msgCode);
	        NetUtil msgCodeVerifyUtil = new HttpClientImplUtil(context, msgVerifyCodeUrl);
	        String msgCodeVerifyResult = msgCodeVerifyUtil.doGet();        
			return ServerDataUtils.parseToJson(msgCodeVerifyResult);
        }
	}
}
