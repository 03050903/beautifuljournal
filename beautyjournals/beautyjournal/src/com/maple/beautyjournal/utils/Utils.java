/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maple.beautyjournal.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;

import com.maple.beautyjournal.fragment.ArticleListFragment;
import com.maple.beautyjournal.fragment.HomeFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private Utils() {}



    @TargetApi(11)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll()
                    .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder.setClassInstanceLimit(ArticleListFragment.class, 1)
                        .setClassInstanceLimit(HomeFragment.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
//        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
        return false;
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void writeAvatarFile(Context context, byte[] data) {
        FileOutputStream fos;
        try {
            File dir = context.getCacheDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = getAvatarFile(context);
            fos = new FileOutputStream(f);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Drawable getAvatarDrawableFromFile(Context context) {
        FileInputStream fis = null;
        try {
            File f = getAvatarFile(context);
            if(!f.exists()) {
                return null;
            }
            fis = new FileInputStream(f);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
            return new BitmapDrawable(context.getResources(), b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static File getAvatarFile(Context context) {
        return new File(context.getCacheDir(), ConstantsHelper.AVATAR_FILE_NAME);
    }

    public static byte[] getAvatarFileBytes(Context context) {
        File f = getAvatarFile(context);
        if(f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                byte[]data = new byte[fis.available()];
                fis.read(data);
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(fis!=null) {
                    try{
                        fis.close();
                    }catch (Exception e) {

                    }
                }
            }
        }
        return null;
    }
}
