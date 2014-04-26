package com.maple.beautyjournal.base;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;//友盟的分析包

//基础类，用来初始化一些东西

/*
   为了创建一个适用于多版本的action bar，要声明activity继承于‘Sjerlock’(eg,  SherlockActivity, SherlockFragmentActivity)。
  获取action bar的方法是getSupportActionBar()而不来替换getActionbar()。
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {

	protected String TAG="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//TAG = this.getClass().getSimpleName();
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        //友盟的统计，估计是为了统计开启了什么页面，展示了多少回，那种感觉
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //同上
        MobclickAgent.onPause(this);
    }
    //定义返回监听器
    protected OnBackPressedListener mOnBackPressedListener;
    //设置监听
    public void setOnBackPressedListener(OnBackPressedListener listener){
    	mOnBackPressedListener = listener;
    }
}
