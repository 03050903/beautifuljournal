package com.maple.beautyjournal.adapter;


import com.maple.beautyjournal.R;
import com.maple.beautyjournal.fragment.GuideFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/*
Fragment 成为ViewPager的一页时,由FragmentManager来管理Fragment的创建和销毁
自定义GuideGragmentAdapter,用来填充ViewPager
 */
public class GuideFragmentAdapter extends FragmentPagerAdapter  {

	public GuideFragmentAdapter(FragmentManager fm) {
		super(fm);
	}
	//将图片向导页作为static类型资源，在GuideFragemnt里面引用
    public static final int[] Icons = new int[] {
        R.drawable.guide1,
        R.drawable.guide2,
        R.drawable.guide3};

    public static final int[] Text_Reses = new int[]{
    	R.drawable.guide1_text,
    	R.drawable.guide2_text,
    	R.drawable.guide3_text};
    
    public static final int[] Text_bg_Reses = new int[]{
    	R.color.guide1_text_bg,
    	R.color.guide2_text_bg,
    	R.color.guide3_text_bg};

   //返回一个Fragment页，得到一个Item
	@Override
	public Fragment getItem(int pos) {
		return GuideFragment.newInstance(pos);
	}
    //得到页面的总数，也就是Guide页面展示的总页数
	@Override
	public int getCount() {
		return 4;
	}

}
