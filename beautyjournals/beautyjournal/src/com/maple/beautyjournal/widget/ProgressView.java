package com.maple.beautyjournal.widget;



import com.maple.beautyjournal.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {
    
    Bitmap dark;
    Bitmap color;
    int width;
    int height;
    int value;
    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dark = getBitmap(R.drawable.progress_icon_dark);
        width = dark.getWidth();
        height = dark.getHeight();
        color = getBitmap(R.drawable.progress_icon_color);
        value=0;
    }
    public int getRealWidth(){
        return width;
    }
    public int getRealHeight(){
        return height;
    }
    public ProgressView(Context context) {
        this(context,null);
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(dark, 0, 0, null);
        drawColor(canvas);
        
    }
    
    private void drawColor(Canvas canvas){
        
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        canvas.save();
        canvas.clipRect(0, h-value, w, h);
        canvas.drawBitmap(color, 0, 0, null);
        canvas.restore();
    }
    public void setValue(int value){
        this.value=value;
        invalidate();
    }
    
    private Bitmap getBitmap(int resId) {
        Resources rec = getResources();

        BitmapDrawable bitmapDrawable = (BitmapDrawable) rec.getDrawable(resId);

        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);

        int measuredWidth = measureWidth(widthMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);


    }

    private int measureHeight(int measureSpec) {

        return height;
    }

    private int measureWidth(int measureSpec) {
       
        return width;
    }
    public void onDestroy() {
        if(dark!=null){
            dark.recycle();
            dark=null;
        }
        if(color!=null){
            color.recycle();
            color=null;
        }
    }
}
