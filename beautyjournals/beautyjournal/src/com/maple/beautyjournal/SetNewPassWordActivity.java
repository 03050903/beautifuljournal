package com.maple.beautyjournal;

import android.content.Intent;
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
import com.maple.beautyjournal.utils.ConstantsHelper;

public class SetNewPassWordActivity extends BaseActivity{

	private EditText mNewPassword;
	private EditText mcomfirmpassword;
	private Button mFinish;
    private String newPassword;
    private String comfirmpassword;
	
    private String mPhoneNumber;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_newpassword);
        
        this.mPhoneNumber = this.getIntent().getExtras().getString(ConstantsHelper.PHONE_NUMBER);
        this.mNewPassword = (EditText) this.findViewById(R.id.newpassword);
        this.mcomfirmpassword = (EditText) this.findViewById(R.id.comfirmpassword);

        this.mFinish = (Button) this.findViewById(R.id.btn_finish);
        this.mFinish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//step1 check password null
				newPassword = mNewPassword.getEditableText().toString().trim();
				if(TextUtils.isEmpty(newPassword)){
					Toast.makeText(SetNewPassWordActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(TextUtils.isEmpty(newPassword) || newPassword.length()<6){
					Toast.makeText(SetNewPassWordActivity.this, "新密码不能少于6位", Toast.LENGTH_SHORT).show();
					return;					
				}
				//step2 check comfirmpassword null
				comfirmpassword = mcomfirmpassword.getEditableText().toString().trim();
				if(TextUtils.isEmpty(comfirmpassword)){
					Toast.makeText(SetNewPassWordActivity.this, "请确认新密码", Toast.LENGTH_SHORT).show();
					return;
				}
				//step3 check password equal
				if(!newPassword.equals(comfirmpassword)){
					Toast.makeText(SetNewPassWordActivity.this, "输入密码不相同", Toast.LENGTH_SHORT).show();
					return;
				}
				//step4 
				new changePassWordTask().execute();
				
				
			}
		});
	}
	class changePassWordTask extends AsyncTask<Object, Object, AccessorResultWrapper>{

		@Override
		protected AccessorResultWrapper doInBackground(Object... arg0) {			
			 AccessorResultWrapper result = UserAccessor.doUpdatePasswdByPhone(SetNewPassWordActivity.this, mPhoneNumber, newPassword);
			return result;
		}
		
		@Override
		protected void onPreExecute(){
			mNewPassword.setEnabled(false);
			mcomfirmpassword.setEnabled(false);
			showProgress();
		}
		
		@Override
		protected void onPostExecute(AccessorResultWrapper result){
			mNewPassword.setEnabled(true);
			mcomfirmpassword.setEnabled(true);
			dismissProgress();
			
			if(result.isSuccess){
			    Toast.makeText(SetNewPassWordActivity.this, R.string.update_passwd_succ, Toast.LENGTH_SHORT).show();	
				Intent logInIntent =new Intent(SetNewPassWordActivity.this,LoginActivity.class);
				startActivity(logInIntent);
			}else{
			    Toast.makeText(SetNewPassWordActivity.this, result.errorMsg, Toast.LENGTH_SHORT).show();	
			}
		}
	}
    public void onBack(View v) {
        onBackPressed();
    }

}
