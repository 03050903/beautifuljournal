package com.maple.beautyjournal.widget;


import com.maple.beautyjournal.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public class ProgressAnimateView extends RelativeLayout{

	public ProgressAnimateView(Context context) {
		super(context);
	    View v = LayoutInflater.from(context).inflate(R.layout.progress_animate_view, this, true);
	    progressView = (ProgressView) v.findViewById(R.id.progressView);
	}

	public ProgressAnimateView(Context context, AttributeSet attrs) {
		super(context, attrs);
	    View v = LayoutInflater.from(context).inflate(R.layout.progress_animate_view, this, true);
	    progressView = (ProgressView) v.findViewById(R.id.progressView);
	}

	private ProgressView progressView = null;
    float min;
    float max;
    int value;
    int height;
    private ProgressHandler handler;

    public void start(){
    	this.value = 0;
    	this.min = 0;
    	this.height = progressView.getRealHeight();
    	this.max = progressView.getRealHeight();
        handler = new ProgressHandler();
        handler.sendEmptyMessageDelayed(0,10);
        this.setVisibility(View.VISIBLE);
    }

    public void end(){
    	handler.sendEmptyMessageDelayed(1, 10);
    	this.setVisibility(View.GONE);
    }
    
    public void setNewValue(float v) {
        if (v < min) {
            v = min;
        } else if (v > max) {
            v = max;
        }
        v = v / (max - min) * height;
        value = (int) v;
        this.post(new Runnable() {
            @Override
            public void run() {
            	progressView.setValue(value);
            }
        });

    }

    private class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    value += 2;
                    if (value < height - 6) {
                    	setNewValue(value);
                        sendEmptyMessageDelayed(0, 10);
                    }else{
                    	value = 0;
                        sendEmptyMessageDelayed(0, 10);
                    }
                    break;
                case 1:
                	setNewValue(height);
                    sendEmptyMessageDelayed(2, 500);
                    break;
                case 2:
                    //dismiss();
                    break;
            }

        }
    }
}
