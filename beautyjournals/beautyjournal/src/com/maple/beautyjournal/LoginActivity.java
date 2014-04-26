package com.maple.beautyjournal;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.accessor.AccessorResultWrapper;
import com.maple.beautyjournal.accessor.UserAccessor;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.UserInfo;
import com.maple.beautyjournal.utils.AuthUtils;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.utils.AuthUtils.AuthType;
import com.maple.beautyjournal.widget.ProgressWindow;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.UsersAPI;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener,
        ProgressWindow.OnDismissListener {
    private EditText usernameEdit, passwordEdit;
    private Button logInButton;
    private TextView sinaLogInButton, qqLogInButton;
    private String errorMsg = null;
    private WeiboAuth mWeibo;
    private Oauth2AccessToken accessToken;
    private SsoHandler mSsoHandler;
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEdit = (EditText) findViewById(R.id.username);
        passwordEdit = (EditText) findViewById(R.id.password);

        if (ConstantsHelper.TEST) {
            usernameEdit.setText("ty1381200440");
            passwordEdit.setText("123456");
        }
        
        String lastUserName = SettingsUtil.getLastUserName(this);
        if(!TextUtils.isEmpty(lastUserName)){
            usernameEdit.setText(lastUserName); 
            usernameEdit.setSelection(lastUserName.length());
        }
        logInButton = (Button) findViewById(R.id.logInButton);
        logInButton.setOnClickListener(this);
        sinaLogInButton = (TextView) findViewById(R.id.log_in_with_sina);
        sinaLogInButton.setOnClickListener(this);
        
        qqLogInButton = (TextView) findViewById(R.id.log_in_with_qq);
        qqLogInButton.setOnClickListener(this);
        initWeibo();
        initTencent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
        UserInfo user = SettingsUtil.getUser(LoginActivity.this);
        if(user !=null && !TextUtils.isEmpty(user.id) && !TextUtils.isEmpty(user.name)){
        	setResult(RESULT_OK);
        	finish();
        }
    }
    
    private void initTencent() {
        mTencent = Tencent.createInstance(ConstantsHelper.TencentAppId, getApplicationContext());
        if(mTencent == null){
        	Log.e("initTencent", "failed");
        }    
    }

    private void initWeibo() {    	
    	mWeibo = new WeiboAuth(this, ConstantsHelper.SinaAppKey, ConstantsHelper.SinaAppRedirectURI, ConstantsHelper.SinaScope);
        mSsoHandler = new SsoHandler(this, mWeibo);
    }

    public void doRegister(View arg0) {
        Intent intent = new Intent(this, RegisterIdentifyActivity.class);
        startActivityForResult(intent, 0);
    }
    public void doFind(View arg0) {
        Intent intent = new Intent(this, FindPassWordActivity.class);
        startActivityForResult(intent, 1);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + ", " + resultCode + ", " + data);

        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivity, success!");
            Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
            try {
                if (mSsoHandler != null) {
                    mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logInButton:
                if (validateFields()) {
                    new LogInTask().execute();
                }
                break;
            case R.id.log_in_with_sina:
                mSsoHandler.authorize(this.mWeiBoAuthListener);
                break;
            case R.id.log_in_with_qq:
                mTencent.login(this, "all", this.mQQAuthListener);
                break;
        }
    }

    @Override
    public void onDismiss() {
        //TODO
    }

    
    private void doLogon(String userName, String passwd){    	
    	AccessorResultWrapper result = UserAccessor.doLogon(LoginActivity.this, userName, passwd);    	
        if (result.isSuccess) {
            errorMsg = null;
            JSONObject obj;
			try {
				obj = result.result.getJSONObject("info");
	            UserInfo user = ServerDataUtils.getUserInfoFromJSONObject(obj);
	            SettingsUtil.saveUser(LoginActivity.this, user);
	            ServerDataUtils.getAddressListFromServer(LoginActivity.this);
			} catch (JSONException e) {
				errorMsg = e.getLocalizedMessage();
				e.printStackTrace();
			}
        } else {
            errorMsg = result.errorMsg;
        }
    }
    
    private class LogInTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(LoginActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {        	
        	LoginActivity.this.doLogon(usernameEdit.getText().toString()
        			, passwordEdit.getText().toString());
            return null;
        }
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(usernameEdit.getText().toString())) {
            Toast.makeText(this, R.string.error_username_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(passwordEdit.getText().toString())) {
            Toast.makeText(this, R.string.error_password_empty, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public void onBack(View v) {
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    
    IUiListener mQQAuthListener = new IUiListener(){

        @Override
        public void onCancel() {

        }

        @Override
        public void onComplete(JSONObject object) {
            try {
                Log.d(TAG, "onTencentComplete, object is " + object.toString());
                String token = object.optString("access_token");
                String openId = object.optString("openid");
                String expireTime = object.optString("expires_in");
                Oauth2AccessToken qqAccessToken = new Oauth2AccessToken(token, expireTime);
                if(qqAccessToken.isSessionValid()) {
                	mTencent.setAccessToken(token, expireTime);
                	mTencent.setOpenId(openId);
                    String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date
                                                                                             (qqAccessToken                                                                                                      .getExpiresTime()));
                    Log.d(TAG, "QQ log in, expire date is " + date);
                    SettingsUtil.saveQQAccessToken(LoginActivity.this, qqAccessToken);
                    SettingsUtil.saveQQUid(LoginActivity.this, openId);
                    new UpdateUserInfoTask(AuthType.QQ).execute();

                }
                SettingsUtil.saveQQLogInfo(LoginActivity.this, object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, getString(R.string.log_in_failed), Toast.LENGTH_SHORT).show();
        }
    	
    };
    
    WeiboAuthListener mWeiBoAuthListener =new WeiboAuthListener(){

        @Override
        public void onComplete(Bundle values) {
            try {
                for (String key : values.keySet()) {
                    Log.d(TAG, key + " : " + values.get(key).toString());
                }
                String token = values.getString("access_token");
                String expires_in = values.getString("expires_in");
                String id = values.getString("uid");
                accessToken = new Oauth2AccessToken(token, expires_in);
                if (accessToken.isSessionValid()) {
                    String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date
                                                                                             (accessToken
                                                                                                      .getExpiresTime()));
                    Log.d(TAG, "weibo log in, expire date is " + date);
                    SettingsUtil.saveWeiboAccessToken(LoginActivity.this, accessToken);
                    SettingsUtil.saveWeiboUid(LoginActivity.this, id);
                    
                    new UpdateUserInfoTask(AuthType.Sina).execute();
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, getString(R.string.log_in_failed), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancel() {

        }

		@Override
		public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this, String.format(getString(R.string.weibo_error), e.getMessage()),
                    Toast.LENGTH_LONG).show();
		}

    	
    };

    private class UpdateUserInfoTask extends AsyncTask<Void, Void, Void> {
    	
    	private Object mLocker = new Object();
    	private AuthType mAuth;
    	public UpdateUserInfoTask(AuthType auth){
    		mAuth = auth;
    	}
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            super.onPostExecute(aVoid);
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
        	
        	final UserInfo snsUserInfo = new UserInfo();
    		Runnable run = new Runnable(){

				@Override
				public void run() {
	            	if(AuthType.QQ.equals(mAuth)){
						mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null,
				                Constants.HTTP_GET, new IRequestListener(){
			
							@Override
							public void onComplete(JSONObject json, Object arg1) {							
								AuthUtils.parseUserInfoFromQQ(json, snsUserInfo);	
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}
			
							@Override
							public void onConnectTimeoutException(
									ConnectTimeoutException arg0, Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}									
							}
			
							@Override
							public void onHttpStatusException(HttpStatusException arg0,
									Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}								
							}
			
							@Override
							public void onIOException(IOException arg0, Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}
			
							@Override
							public void onJSONException(JSONException arg0, Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}
			
							@Override
							public void onMalformedURLException(MalformedURLException arg0,
									Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}
			
							@Override
							public void onNetworkUnavailableException(
									NetworkUnavailableException arg0, Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}
			
							@Override
							public void onSocketTimeoutException(
									SocketTimeoutException arg0, Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}
			
							@Override
							public void onUnknowException(Exception arg0, Object arg1) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}}, null);
	            	}else if (AuthType.Sina.equals(mAuth)){
	            	    // dosomething here
	            		Oauth2AccessToken sinaAccessToken = SettingsUtil.readWeiboAccessToken(LoginActivity.this);
	            		String uid = SettingsUtil.getWeiboUid(LoginActivity.this);
	            		long luid = Long.parseLong(uid);
	            		UsersAPI usersAPI = new UsersAPI(sinaAccessToken);
	            		
	            		usersAPI.show(luid, new RequestListener(){
							@Override
							public void onComplete(String result) {	
								
								AuthUtils.parseUserInfoFromSina(result, snsUserInfo);
								synchronized(mLocker){
								    mLocker.notifyAll();
								}

							}

							@Override
							public void onComplete4binary(
									ByteArrayOutputStream arg0) {								
							}

							@Override
							public void onError(WeiboException arg0) {
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}

							@Override
							public void onIOException(IOException arg0) {								
								synchronized(mLocker){
								    mLocker.notifyAll();
								}
							}});
	            	}						
				}
    			
    		};
    		Thread thread = new Thread(run);
    		thread.start();			
			try {
	        	synchronized(mLocker){
	        		mLocker.wait();
	        	}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            errorMsg = null;	
        	if(snsUserInfo != null && !TextUtils.isEmpty(snsUserInfo.name)){
	            Context context = LoginActivity.this;
	            
	            AccessorResultWrapper validateUserResult = UserAccessor.doValidateUserUnique(context, snsUserInfo.name);                
                if (validateUserResult.isSuccess) {
                    String isOk = validateUserResult.result.optString("info");
                    if(!TextUtils.isEmpty(isOk) && "true".equals(isOk)){
                    	//register
        	            String url = NetUtil.getRegisterUrl(context);
        	            NetUtil util = new HttpClientImplUtil(context, url);
        	            Map<String, String> registerMap = new HashMap<String, String>();
        	            registerMap.put("username", snsUserInfo.name);
        	            registerMap.put("passwd", AuthUtils.getAuthRelatedPassword(mAuth));
        	            registerMap.put("gender", String.valueOf(snsUserInfo.gender));
        	            registerMap.put("platform", "Android");
        	            registerMap.put("imagename", ""+snsUserInfo.imagename);
        	            util.setMap(registerMap);
        	            String result = util.uploadMultiPart();
        	            try {
        	                JSONObject registerJson = new JSONObject(result);
        	                if (ServerDataUtils.isTaskSuccess(registerJson)) {
        	                    String id = registerJson.optJSONObject("info").optString("id");
        	                    SettingsUtil.saveUserInfo(context, snsUserInfo.name, id, snsUserInfo.gender);
        	                } else {
        	                    errorMsg = ServerDataUtils.getErrorMessage(registerJson);
        	                }
        	            } catch (Exception e) {
        	                errorMsg = e.getLocalizedMessage();
        	            }
                    }else{
                    	//logon
                    	LoginActivity.this.doLogon(snsUserInfo.name, AuthUtils.getAuthRelatedPassword(mAuth));
                    }
                } else {
                    errorMsg = validateUserResult.errorMsg;
                }	                
        	}
            return null;
        }
    }


}
