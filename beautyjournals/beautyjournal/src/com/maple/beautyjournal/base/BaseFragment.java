package com.maple.beautyjournal.base;

import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragment;
import com.maple.beautyjournal.widget.ProgressWindow;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends SherlockFragment{

	protected String TAG="";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
	}
	
    @Override
	public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
	public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    
    //Progress Window
    protected void dismissProgress() {
        if (progressWindow != null) {
            progressWindow.dismiss();
            progressWindow = null;
        }
    }
    
    protected void showProgress(){
    	showProgress(null);
    }

    protected void showProgress(ProgressWindow.OnDismissListener onDismissListener){
        if (progressWindow == null) {
        	Display display = getActivity().getWindowManager().getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            progressWindow = new ProgressWindow(getActivity(), screenWidth, screenHeight, onDismissListener);
        }    	
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        progressWindow.show(rootView);
    }
    protected void showProgressWithRoot(View contentView, ProgressWindow.OnDismissListener onDismissListener) {
        if (progressWindow == null) {
        	Display display = getActivity().getWindowManager().getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            progressWindow = new ProgressWindow(getActivity(), screenWidth, screenHeight, onDismissListener);
        }
        progressWindow.show(contentView);
    }
    
    ProgressWindow progressWindow;

}
