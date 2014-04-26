package com.maple.beautyjournal.widget;

import com.maple.beautyjournal.R;
import com.maple.beautyjournal.R.drawable;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

public class ProgressWindow {
    Context context;
    PopupWindow popWindow;
    ProgressView progress;
    float min;
    float max;
    public int height;
    int width;
    int screenWidth;
    int screenHeight;
    int value;
    private ProgressHandler handler;
    private OnDismissListener listener;

    public ProgressWindow(Context context, int screenW, int screenH, OnDismissListener listener) {
        this.context = context;
        progress = new ProgressView(context);
        handler = new ProgressHandler();
        width = progress.getRealWidth();
        height = progress.getRealHeight();
        min = 0;
        value = 0;
        max = height;
        screenWidth = screenW;
        screenHeight = screenH;
        this.listener = listener;
    }

    private PopupWindow createWindow() {
        PopupWindow window = new PopupWindow(context);
        FrameLayout frame = new FrameLayout(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                                                                   LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        frame.addView(progress, lp);


        window.setContentView(frame);
        window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.trans));
//        int screenWidth  = context.getWindowManager().getDefaultDisplay().getWidth();    
//        int screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
        window.setWidth(screenWidth);
        window.setHeight(screenHeight);

        return window;
    }

    public void show(View parent) {
        if (popWindow == null) {
            popWindow = createWindow();
        }
        try {
            popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } catch (Exception e) {

        }
        handler.sendEmptyMessage(0);
    }

    public void dismissProgres() {
        handler.sendEmptyMessageDelayed(1, 10);
    }

    public void dismiss() {
        try {
            if (popWindow != null && popWindow.isShowing()) {
                popWindow.dismiss();
                value = 0;
                if (listener != null) { listener.onDismiss(); }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setValue(float v) {
        if (v < min) {
            v = min;
        } else if (v > max) {
            v = max;
        }
        v = v / (max - min) * height;
        value = (int) v;
        progress.post(new Runnable() {

            @Override
            public void run() {
                progress.setValue(value);
            }

        });

    }

    public void onDestroy() {
        if (progress != null) {
            progress.onDestroy();
        }
    }

    private class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    value += 2;
                    if (value < height - 6) {
                        setValue(value);
                        sendEmptyMessageDelayed(0, 10);
                    }else{
                    	value = 0;
                        sendEmptyMessageDelayed(0, 10);
                    }
                    break;
                case 1:
                    setValue(height);
                    sendEmptyMessageDelayed(2, 500);
                    break;
                case 2:
                    dismiss();
                    break;
            }

        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
