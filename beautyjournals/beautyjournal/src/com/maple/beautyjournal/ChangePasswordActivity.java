package com.maple.beautyjournal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maple.beautyjournal.accessor.AccessorResultWrapper;
import com.maple.beautyjournal.accessor.UserAccessor;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.UserInfo;
import com.maple.beautyjournal.utils.SettingsUtil;

public class ChangePasswordActivity extends BaseActivity{

	private EditText mOldPasswdView;
	private EditText mNewPasswdView;
	private EditText mRepeatNewPasswdView;
	
	private Button mBtnSave;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        
        this.mOldPasswdView = (EditText) this.findViewById(R.id.password_old);
        this.mNewPasswdView = (EditText) this.findViewById(R.id.password_new);
        this.mRepeatNewPasswdView = (EditText) this.findViewById(R.id.password_repeatnew);
        
        this.mBtnSave = (Button) this.findViewById(R.id.btn_save);
        this.mBtnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String oldPassword = mOldPasswdView.getEditableText().toString().trim();
				final String newPassword = mNewPasswdView.getEditableText().toString().trim();
				final String repeatNewPasswd = mRepeatNewPasswdView.getEditableText().toString().trim();

				//step1 check old password not null
				if(TextUtils.isEmpty(oldPassword)){
					Toast.makeText(ChangePasswordActivity.this, "旧密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(newPassword) || newPassword.length()<6){
					Toast.makeText(ChangePasswordActivity.this, "新密码不能少于6位", Toast.LENGTH_SHORT).show();
					return;					
				}
				//step2 check new and repeatnew equals
				if(!newPassword.equals(repeatNewPasswd)){
					Toast.makeText(getBaseContext(), "重复密码与新密码不一致", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				new AsyncTask<Object, Object, AccessorResultWrapper>(){

					@Override
					protected void onPreExecute(){
						mBtnSave.setEnabled(false);
						showProgress();					
					}
					
					@Override
					protected AccessorResultWrapper doInBackground(Object... params) {
						Context context = ChangePasswordActivity.this;
						UserInfo user = SettingsUtil.getUser(context);
						user.passwd = newPassword;
						//step1 check old password ok
						AccessorResultWrapper result = UserAccessor.doLogon(context, user.name, oldPassword);
						if(result.isSuccess){
						    //step2 update new password
							result = UserAccessor.doUpdateUser(context, user);
						}else{
							result.errorMsg = "请输入正确的旧密码";
						}
						return result;
					}
					
					@Override
					protected void onPostExecute(AccessorResultWrapper updateResult){						
						mBtnSave.setEnabled(true);
			            dismissProgress();
						if(updateResult.isSuccess){
							Toast.makeText(getBaseContext(), "修改密码成功", Toast.LENGTH_SHORT).show();
							setResult(RESULT_OK);
							finish();
						}else{
							Toast.makeText(getBaseContext(), updateResult.errorMsg, Toast.LENGTH_SHORT).show();
						}
					}
					
				}.execute();
				
			}
		});
    }
	
    public void onBack(View v) {
        onBackPressed();
    }

}
