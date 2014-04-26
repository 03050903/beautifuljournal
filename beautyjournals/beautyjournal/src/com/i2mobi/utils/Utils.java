package com.i2mobi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;

public class Utils {
    public final static String DIR_PIC_STORE = "sdcard/kiicoupon/";
    public static boolean hasNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        return info.isConnected();
    }
    public static boolean hasSDCard() {
        String t = android.os.Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(t);
    }
    public static boolean fileExist(String path) {
        if (!hasSDCard()) {
            return false;
        }
        String name = path.hashCode() + "." + IOUtil.getPostfix(path);
        File file = new File(DIR_PIC_STORE + name);
        return file.exists();
    }


    public static File getCacheDir(Context context) {
        if(hasSDCard()) {
            return context.getExternalCacheDir();
        } else {
            return context.getCacheDir();
        }
    }
}
