package com.i2mobi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.maple.beautyjournal.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tian on 13-7-12.
 */
public class ImageUtils{

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {

                }
            }
        }
        return null;
    }

    public static Bitmap getBitmapFromBytes(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Constructs an intent for image cropping.
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", ICON_SIZE);
        intent.putExtra("outputY", ICON_SIZE);
        intent.putExtra("return-data", true);
        return intent;
    }

    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", ICON_SIZE);
        intent.putExtra("outputY", ICON_SIZE);
        intent.putExtra("return-data", true);
        return intent;
    }

    public static final int ICON_SIZE = 150;

    @SuppressLint("SimpleDateFormat")
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    public static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory()
                    + "/DCIM/Camera/com.kii.recipe");

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    public static File saveBitmapAsFile(Bitmap b) {
        android.util.Log.d("ImageUtil", "saveBitmapAsFile, b is empty?" + (b == null));
        byte[] data = getBytesFromBitmap(b);
        File f = new File(PHOTO_DIR + "/" + getPhotoFileName());
        FileOutputStream os = null;
        try {
            android.util.Log.d("ImageUtil", "filename is " + f.getAbsolutePath());
            os = new FileOutputStream(f);
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return f;
    }

    public static void saveUploadImage(Bitmap b, Context context) {
        if (b != null) {
            final ByteArrayOutputStream cacheOutput = new ByteArrayOutputStream(8192);
            try {
                b.compress(Bitmap.CompressFormat.PNG, 85, cacheOutput);
                byte[] data = cacheOutput.toByteArray();
                com.maple.beautyjournal.utils.Utils.writeAvatarFile(context, data);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    cacheOutput.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}