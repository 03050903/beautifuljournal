package com.maple.beautyjournal.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingWorkSpaceUtils {

	private static final String IS_GUIDE_DISPLAYED = "IS_GUIDE_DISPLAYED";  //导航是否展示
    //返回boolean型，如果展示过后返回true
    public static Boolean getIsGuideDisplayed(Context context) {
        return SettingsUtil.getPrefs(context).getBoolean(IS_GUIDE_DISPLAYED, false);
    }
    //设置是否展示过向导界面，将IS_GUIDE_DISPLAY,key——value的形式存储起来

    public static void setIsGuideDisplayed(Context context, Boolean isGuideDisplayed) {
        SharedPreferences pref = SettingsUtil.getPrefs(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_GUIDE_DISPLAYED, isGuideDisplayed);
        editor.commit();
    }

}
