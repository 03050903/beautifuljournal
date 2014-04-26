package com.maple.beautyjournal.utils;

import android.os.CountDownTimer;
import android.widget.Button;

public 	class TimeCount extends CountDownTimer{
	private Button mGetWord;
	public TimeCount(Button buttonMsgCode, long millisInFuture, long countDownInterval){
		super(millisInFuture,countDownInterval);
		this.mGetWord = buttonMsgCode;
	}
	
	@Override
	public void onFinish(){
		mGetWord.setText("重新获取验证码");
		mGetWord.setEnabled(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		mGetWord.setEnabled(false);
		mGetWord.setText(String.valueOf(millisUntilFinished/1000)+"秒");		
	}	
}

