package com.maple.beautyjournal;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.maple.beautyjournal.adapter.GuideFragmentAdapter;
import com.maple.beautyjournal.base.BaseFragmentActivity;
import com.viewpagerindicator.CirclePageIndicator;


public class GuideActivity extends BaseFragmentActivity  {

	private GuideFragmentAdapter mAdapter;
	private ViewPager mPager;
	private CirclePageIndicator mIndicator;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //得到actionbar，并将其隐藏
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        setContentView(R.layout.activity_guide);
        
        mAdapter = new GuideFragmentAdapter(getSupportFragmentManager());
        //android.support.v4.view.ViewPager展示页，
        mPager = (ViewPager)findViewById(R.id.pager);
        //设置到Adapter之后就由Adapter自己处理滑动事件
        mPager.setAdapter(mAdapter);
        //这个应该是滑动的那个黑点，网上这么说得
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}
