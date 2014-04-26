package com.maple.beautyjournal;


import com.actionbarsherlock.app.ActionBar;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.utils.SettingWorkSpaceUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
/*
欢迎界面
 */
public class WelcomeActivity extends BaseActivity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.welcome);
    
    ActionBar bar = getSupportActionBar();
    if (bar != null) { //若actionbar为空的话就将其隐藏
        bar.hide();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
      //用Handler来延迟跳转，大概在1秒之后执行prepareNextActivity
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        WelcomeActivity.this.prepareNextActivity();
      }

    }, 1000);
  }

  private void prepareNextActivity() {
      //判断，如果已经展示过了就不用展示了
	if(SettingWorkSpaceUtils.getIsGuideDisplayed(this)){
        //启动MainActivity
	    Intent it = new Intent(this, MainActivity.class);
	    this.startActivity(it);
	    WelcomeActivity.this.finish();
	    this.overridePendingTransition(
	        R.anim.push_right_out,
	        R.anim.push_right_out);
	}
	else{
        //启动GuideActivity
		Intent it = new Intent(this, GuideActivity.class);
	    this.startActivity(it);
	    WelcomeActivity.this.finish();
	    this.overridePendingTransition(
	        R.anim.push_right_out,
	        R.anim.push_right_in);
	}
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}
