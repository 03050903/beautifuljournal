package com.maple.beautyjournal;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maple.beautyjournal.accessor.AccessorResultWrapper;
import com.maple.beautyjournal.accessor.MessageCodeAccessor;
import com.maple.beautyjournal.accessor.MessageCodeAccessor.MsgCodeTypeEnum;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.TimeCount;

public class RegisterIdentifyActivity extends BaseActivity{

	private EditText mPhoneNum;
	private EditText mCheckWord;
	private Button mGetWord;
	
	private Button mBtnNext;
	private String phoneNum;
	private String checkWord;
    private CountDownTimer timeCount;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_check);
        this.mPhoneNum = (EditText) this.findViewById(R.id.phoneNum);
        this.mCheckWord = (EditText) this.findViewById(R.id.checkWord);
        
        this.mGetWord= (Button) this.findViewById(R.id.btn_getword);
        this.mGetWord.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//step1 check phoneNum not null
				phoneNum = mPhoneNum.getEditableText().toString().trim();
				if(TextUtils.isEmpty(phoneNum)){
					Toast.makeText(RegisterIdentifyActivity.this, "请输入电话号码", Toast.LENGTH_SHORT).show();
					return;
				}
				//step2 request Message code
				new reqMessageCodeTask().execute();
				
			}
		});
        this.mBtnNext = (Button) this.findViewById(R.id.btn_nextstep);
        this.mBtnNext.setOnClickListener(new View.OnClickListener() { 
			
			@Override
			public void onClick(View v) {

				//step1 check phoneNum not null
				phoneNum = mPhoneNum.getEditableText().toString().trim();
				if(TextUtils.isEmpty(phoneNum)){
					Toast.makeText(RegisterIdentifyActivity.this, "请输入电话号码", Toast.LENGTH_SHORT).show();
					return;
				}
				//step2 check identifing code not null
				checkWord = mCheckWord.getEditableText().toString().trim();
				if(TextUtils.isEmpty(checkWord)){
					Toast.makeText(RegisterIdentifyActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//step3 check identifingcode
				new checkMessageCodeTask().execute();
			}
		}
        );
    }
	
	class reqMessageCodeTask extends AsyncTask<Object, Object, AccessorResultWrapper>{

		@Override
		protected AccessorResultWrapper doInBackground(Object... arg0) {
			AccessorResultWrapper result = MessageCodeAccessor.getMsgCode(RegisterIdentifyActivity.this,MsgCodeTypeEnum.TypeRegister, phoneNum);
			return result;
		}
		
		@Override
		protected void onPreExecute(){
			mGetWord.setEnabled(true);
			timeCount = new TimeCount(mGetWord,60000,1000).start();
		}
		
		@Override
		protected void onPostExecute(AccessorResultWrapper result){
			
			if(!result.isSuccess){
				Toast.makeText(RegisterIdentifyActivity.this, result.errorMsg, Toast.LENGTH_SHORT).show();
				timeCount.cancel();
				mGetWord.setEnabled(true);
				mGetWord.setText("重新获取验证码");
			}			
		}
	}
	
	class checkMessageCodeTask extends AsyncTask<Object, Integer, AccessorResultWrapper>{

		@Override
		protected AccessorResultWrapper doInBackground(Object... arg0) {
			AccessorResultWrapper message = MessageCodeAccessor.verifyMsgCode(RegisterIdentifyActivity.this,phoneNum,checkWord );
			
			return message;
		}
		
		@Override
		protected void onPreExecute(){
			mBtnNext.setEnabled(false);
			showProgress();
		}
		
		
		@Override
		protected void onPostExecute(AccessorResultWrapper result){
			mBtnNext.setEnabled(true);
			dismissProgress();

			if (!result.isSuccess){
				Toast.makeText(RegisterIdentifyActivity.this, result.errorMsg, Toast.LENGTH_SHORT).show();
				return;
			}
	        Intent intent = new Intent(RegisterIdentifyActivity.this, RegisterActivity.class);
	        intent.putExtra(ConstantsHelper.PHONE_NUMBER, phoneNum);
	        startActivityForResult(intent, 0);

		}
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			setResult(RESULT_OK);
			finish();
		}
	}
		
    public void onBack(View v) {
        onBackPressed();
    }


    @Override
    public void onResume(){
    	super.onResume();
    	//TODO(shuyinghuang) will remove later
        //ConstantsHelper.TEST = true;
    }

    @Override
    public void onPause(){
    	super.onResume();
    	//TODO(shuyinghuang) will remove later
        //ConstantsHelper.TEST = false;
    }

}
