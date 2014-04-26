package com.i2mobi.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.i2mobi.utils.ImageUtils;
import com.maple.beautyjournal.R;

import java.io.File;

/**
 * Created by tian on 13-7-12.
 */
public class PhotoChooseView extends ImageView {
    public static final int CAMERA_WITH_DATA = 10000;
    public static final int PHOTO_PICKED_WITH_DATA = 10001;
    public static final int REMOVE_DATA = 10002;
    private Bitmap mPhotoData = null;
    private Context mContext;
    private Fragment mFragment; 
    private File mCurrentPhotoFile;

    public PhotoChooseView(Context context, AttributeSet set) {
        super(context, set);
        mContext = context;
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPickPicDialog();
            }
        });
    }

    public void initFragmentContext(Fragment fragment){
    	mFragment = fragment;
    }
    
    public void showPickPicDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setItems(new String[]{mContext.getString(R.string.title_button_gallery), mContext
                        .getString(R.string.title_button_camera)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                doPickFromGallery();
                                break;
                            case 1:
                                doTakePhoto();
                                break;
                        }
                    }

                }).create();
        dialog.show();
    }

    public void doTakePhoto() {
        try {
            // Launch camera to take photo for selected contact
        	if(!ImageUtils.PHOTO_DIR.exists()){
        		ImageUtils.PHOTO_DIR.mkdirs();
        	}
            mCurrentPhotoFile = new File(ImageUtils.PHOTO_DIR, ImageUtils.getPhotoFileName());
            Log.d("PhotoChooseView", "doTakePhoto, mCurrentPhotoFile is " + mCurrentPhotoFile.getAbsolutePath());
            final Intent intent = ImageUtils.getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, mContext.getString(R.string.cannot_take_photo), Toast.LENGTH_LONG).show();
        }
    }

    public void doPickFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            final Intent intent = ImageUtils.getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "No gallery picker!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setImageBitmap(Bitmap b) {
        super.setImageBitmap(b);
        mPhotoData = b;
    }

    public void doCropPhoto() {
        try {
            // Add the image to the media store
            MediaScannerConnection
                    .scanFile(mContext, new String[]{mCurrentPhotoFile.getAbsolutePath()}, new String[]{null}, null);

            // Launch gallery to crop the photo
            final Intent intent = ImageUtils.getCropImageIntent(Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (Exception e) {
            Log.e("PhotoChooseView", "Cannot crop image", e);
            Toast.makeText(mContext, "Cannot crop image here!", Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap getPhotoData() {
        return mPhotoData;
    }
    
    private void startActivityForResult(Intent intent, int requestCode){
    	if(mFragment!=null){
    		mFragment.startActivityForResult(intent, requestCode);
    	}else if(mContext instanceof Activity){
    		((Activity) mContext).startActivityForResult(intent, requestCode);
    	}else{
    		Log.e("PhotoChooserView", "oh, no impossible");
    	}
    }
}