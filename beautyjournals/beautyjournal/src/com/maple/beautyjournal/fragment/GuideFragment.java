package com.maple.beautyjournal.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.maple.beautyjournal.MainActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.adapter.GuideFragmentAdapter;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.utils.SettingWorkSpaceUtils;

//GuideFragment 应该是一个Fragment 的页面类
public class GuideFragment extends BaseFragment{

	private int mPos=0;
	public static GuideFragment newInstance(int pos){		
		GuideFragment gdFragment = new GuideFragment();
		gdFragment.mPos = pos;
		return gdFragment;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(mPos == 3){    		
	    	View guidView = inflater.inflate(R.layout.guide_item_last, null);
	    	Button guideEnterButton = (Button) guidView.findViewById(R.id.btn_guide_enter);
	    	guideEnterButton.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					Activity mActivity = GuideFragment.this.getActivity();
				    Intent it = new Intent(mActivity, MainActivity.class);
				    mActivity.startActivity(it);
				    mActivity.finish();
				    mActivity.overridePendingTransition(R.anim.push_right_out,
				        R.anim.push_left_out);
				    SettingWorkSpaceUtils.setIsGuideDisplayed(mActivity, true);
				}
			});
	    	return guidView;    		
    	}else{
	    	View guidView = inflater.inflate(R.layout.guide_item, null);	    	
			int mIcon = GuideFragmentAdapter.Icons[mPos];
			int mText = GuideFragmentAdapter.Text_Reses[mPos];
			int mTextBgColor = GuideFragmentAdapter.Text_bg_Reses[mPos];
	
	    	ImageView guideImage = (ImageView) guidView.findViewById(R.id.guide_image);
	    	guideImage.setImageResource(mIcon);	    	
	    	ImageView guideTextView = (ImageView) guidView.findViewById(R.id.guide_text);
	    	guideTextView.setImageResource(mText);	    	
	    	View  guideTextLayout =  guidView.findViewById(R.id.guide_text_layout);
	    	guideTextLayout.setBackgroundResource(mTextBgColor);	    	
	        return guidView;
    	}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
