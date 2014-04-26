package com.maple.beautyjournal.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.maple.beautyjournal.widget.ProgressWindow;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends SherlockActivity {
	
	protected String TAG="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }


    // Activity Result    
    public interface ActivityResultListener {
	    public void onActivityResult(int resultCode, Intent data);
	}

	private SparseArray<ActivityResultListener> listenerMap = new SparseArray<ActivityResultListener>();
	private int count = 0;
    
    /**
     * Start one activity, the requestCode will be handled automatically.
     * @param intent
     *          This intent contains the parameters to be passed to new activity.
     * @param listener
     *          The listener to call when the target activity returned. null if no return
     *          required.
     */
    public void transferActivity(Intent intent, ActivityResultListener listener) {
      if (listener == null) {
        this.startActivity(intent);
      } else {
        int requestCode = this.generateRequestCode();
        this.listenerMap.append(requestCode, listener);
        this.startActivityForResult(intent, requestCode);
      }
    }

    /**
     * Start one activity, the requestCode will be handled automatically.
     * @param cls
     *          This class need to be explicit sent.
     * @param listener
     *          The listener to call when the target activity returned. null if no return
     *          required.
     */
    public void transferActivity(Class<?> cls, ActivityResultListener listener) {
        this.transferActivity(new Intent(this.getApplicationContext(), cls), listener);
    }

    /**
     * Start activity without return expected.
     */
    public void transferActivity(Intent intent) {
        this.transferActivity(intent, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityResultListener listener = this.listenerMap.get(requestCode);
        if (listener != null) {
            listener.onActivityResult(resultCode, data);
            this.listenerMap.remove(requestCode);
        }
    }
    
    private int generateRequestCode() {
        return this.count++;
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
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            progressWindow = new ProgressWindow(this, screenWidth, screenHeight, onDismissListener);
        }    	
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        progressWindow.show(rootView);
    }
    protected void showProgressWithRoot(View contentView, ProgressWindow.OnDismissListener onDismissListener) {
        if (progressWindow == null) {
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            progressWindow = new ProgressWindow(this, screenWidth, screenHeight, onDismissListener);
        }
        progressWindow.show(contentView);
    }
    
    ProgressWindow progressWindow;

}
