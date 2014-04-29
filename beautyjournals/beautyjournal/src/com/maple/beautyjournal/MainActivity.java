package com.maple.beautyjournal;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.base.BaseFragmentActivity;
import com.maple.beautyjournal.broadcast.BootCompleteBroadcast;
import com.maple.beautyjournal.fragment.FavoriteFragment;
import com.maple.beautyjournal.fragment.SettingFragment;
import com.maple.beautyjournal.fragment.ShoppingCarFragment;
import com.maple.beautyjournal.provider.Beauty;
import com.maple.beautyjournal.provider.DatabaseHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends BaseFragmentActivity {

    private FragmentManager fragmentManager;
    private ImageView searchBar;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_home_2);
        initData();// 将数据库复制
        initCompoment();
        fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        HomeFragment homeFragment=new HomeFragment();
        transaction.replace(R.id.content,homeFragment);
        transaction.commit();

        Intent intent = new Intent(BootCompleteBroadcast.ACTION_APPBOOTCOMPLETED);
        sendBroadcast(intent);  //发送广播
    }

    private void initCompoment(){

        searchBar=(ImageView)findViewById(R.id.search);
        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent search=new Intent();
                search.setClass(MainActivity.this,SearchActivity.class);
                startActivity(search);
                return false;
            }
        });
    }


    //初始化一些数据，主要是数据库beauty.db
    private void initData() {
        /*
           这段我没猜错得话应该是讲asset资源里面的beauty.db复制到应用的databases私有文件夹
         */
        new Thread(new Runnable() {
            @Override
            public void run() {

                String filename = MainActivity.this.getDatabasePath(DatabaseHelper.DB_NAME).getAbsolutePath();  //不知道得到什么路径
                Log.d("XXX",filename);
                ///data/data/com.maple.beautyjournal/databases/beauty.db
                File file = new File(filename);
                if (file.exists()) {
                    //如果该文件已经存在则直接返回，说明数据库已经存在了
                    return;
                }
                file.getParentFile().mkdirs();
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = getAssets().open("beauty.db");
                    fos = new FileOutputStream(filename);
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    //退出键
    @Override
    public void onBackPressed() {
        if(this.mOnBackPressedListener != null){
            this.mOnBackPressedListener.doBack();
        }else{
            FragmentManager fm = getSupportFragmentManager();
            if (!fm.popBackStackImmediate()) {
                new AlertDialog.Builder(this).setTitle(R.string.quit_app_prompt)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {


                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult, " + requestCode + ", " + resultCode + ", " + data);
        super.onActivityResult(requestCode, resultCode, data);
    }
//
//    TabHost mTabHost;
//    TabManager mTabManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        //将actionbar隐藏起来
//
//        ActionBar bar = getSupportActionBar();
//        if (bar != null) {
//            bar.hide();
//        }
//
//        setContentView(R.layout.activity_main);
//        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
//        mTabHost.setup();
//        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
//        mTabManager.addTab(buildTabSpec("home", R.string.tab_home, R.drawable.tab_home), HomeFragment.class, null);
//        mTabManager.addTab(buildTabSpec("fav", R.string.tab_fav, R.drawable.tab_fav), FavoriteFragment.class, null);
//        mTabManager.addTab(buildTabSpec("cart", R.string.tab_cart, R.drawable.tab_cart), ShoppingCarFragment.class, null);
//        mTabManager.addTab(buildTabSpec("setting", R.string.tab_setting, R.drawable.tab_setting), SettingFragment.class,null);
//
//        if (savedInstanceState != null) {
//            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
//        }
//        initData();// 将数据库复制
//

//    }
//
//
//    //设置tab界面
//    public void setTab(int id) {
//        mTabHost.setCurrentTab(id);
//    }


//    private TabSpec buildTabSpec(String tag, int resId, int icon) {
//        View view = View.inflate(MainActivity.this, R.layout.tab_item, null);
//        ((ImageView) view.findViewById(R.id.icon)).setImageResource(icon);
//        ((TextView) view.findViewById(R.id.label)).setText(resId);
//        return mTabHost.newTabSpec(tag).setIndicator(view);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("tab", mTabHost.getCurrentTabTag());
//    }
//
//
//    public static class TabManager implements TabHost.OnTabChangeListener {
//        private final FragmentActivity mActivity;
//        private final TabHost mTabHost;
//        private final int mContainerId;
//        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
//        private int[] mTabIconSelected = {R.drawable.tab_home_selected, R.drawable.tab_fav_selected,
//                R.drawable.tab_cart_selected, R.drawable.tab_setting_selected};
//        private int[] mTabIconNormal = {R.drawable.tab_home, R.drawable.tab_fav, R.drawable.tab_cart,
//                R.drawable.tab_setting};
//        TabInfo mLastTab;
//        int mLastTabIndex;
//
//        static final class TabInfo {
//            private final String tag;
//            private final Class<?> clss;
//            private final Bundle args;
//            private Fragment fragment;
//
//            TabInfo(String _tag, Class<?> _class, Bundle _args) {
//                tag = _tag;
//                clss = _class;
//                args = _args;
//            }
//        }
//
//        static class DummyTabFactory implements TabHost.TabContentFactory {
//            private final Context mContext;
//
//            public DummyTabFactory(Context context) {
//                mContext = context;
//            }
//
//            @Override
//            public View createTabContent(String tag) {
//                View v = new View(mContext);
//                v.setMinimumWidth(0);
//                v.setMinimumHeight(0);
//                return v;
//            }
//        }
//
//        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
//            mActivity = activity;
//            mTabHost = tabHost;
//            mContainerId = containerId;
//            mTabHost.setOnTabChangedListener(this);
//        }
//
//        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
//            tabSpec.setContent(new DummyTabFactory(mActivity));
//            String tag = tabSpec.getTag();
//
//            TabInfo info = new TabInfo(tag, clss, args);
//
//            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
//            if (info.fragment != null && !info.fragment.isDetached()) {
//                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
//                ft.detach(info.fragment);
//                ft.commit();
//            }
//
//            mTabs.put(tag, info);
//            mTabHost.addTab(tabSpec);
//        }
//
//        @Override
//        public void onTabChanged(String tabId) {
//            TabInfo newTab = mTabs.get(tabId);
//            int tabIndex = mTabHost.getCurrentTab();
//
//            View tab = mTabHost.getTabWidget().getChildTabViewAt(tabIndex);
//            tab.setBackgroundResource(R.drawable.tab_selected);
//
//            ImageView image = (ImageView) tab.findViewById(R.id.icon);
//            image.setImageResource(mTabIconSelected[tabIndex]);
//            TextView label = (TextView) tab.findViewById(R.id.label);
//            label.setTextColor(Color.WHITE);
//
//            if (mLastTabIndex != tabIndex) {
//                mTabHost.getTabWidget().getChildTabViewAt(mLastTabIndex).setBackgroundColor(Color.TRANSPARENT);
//                ImageView image1 = (ImageView) mTabHost.getTabWidget().getChildTabViewAt(mLastTabIndex)
//                        .findViewById(R.id.icon);
//                image1.setImageResource(mTabIconNormal[mLastTabIndex]);
//
//                TextView label1 = (TextView) mTabHost.getTabWidget().getChildTabViewAt(mLastTabIndex)
//                        .findViewById(R.id.label);
//                label1.setTextColor(Color.BLACK);
//                mLastTabIndex = tabIndex;
//            }
//            if (mLastTab != newTab) {
//                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
//                if (mLastTab != null) {
//                    if (mLastTab.fragment != null) {
//                        while (mLastTab.fragment.getFragmentManager().popBackStackImmediate()) {
//                            ;
//                        }
//                        ft.detach(mLastTab.fragment);
//                    }
//                }
//                if (newTab != null) {
//                    if (newTab.fragment == null) {
//                        newTab.fragment = Fragment.instantiate(mActivity, newTab.clss.getName(), newTab.args);
//                        ft.add(mContainerId, newTab.fragment, newTab.tag);
//
//                    } else {
//                        ft.attach(newTab.fragment);
//                    }
//                }
//
//                mLastTab = newTab;
//
//                ft.commit();
//                mActivity.getSupportFragmentManager().executePendingTransactions();
//            }
//        }
//    }
//
//
    //内部类，获取Data
    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String url = URLConstant.BRAND_LIST_URL;
            Context context = MainActivity.this;
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONArray array = jsonObject.getJSONArray("info");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject brand = array.getJSONObject(i);
                        ContentValues cv = new ContentValues();
                        cv.put(Beauty.Brand.NAME, brand.getString("name"));
                        cv.put(Beauty.Brand.BRAND_ID, brand.getString("id"));
                        cv.put(Beauty.Brand.PINYIN, brand.getString("pinyin"));
                        cv.put(Beauty.Brand.FIRST_CHAR, brand.getString("pinyin"));
                        Uri uri = context.getContentResolver().insert(Beauty.Brand.CONTENT_URI, cv);
                        if (uri != null) {
                            Log.d("MainActivity", "inserted: " + uri);
                        } else {
                            Log.d("MainActivity", "error inserting: " + cv);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //内部类，获取Category
    private class GetCategoryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = URLConstant.CATEGORY_LIST_URL;
            Context context = MainActivity.this;
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONObject info = jsonObject.getJSONObject("info");
                    String cat1Id = "", cat2Id = "", cat3Id = "", cat1Name = "", cat2Name = "", cat3Name = "";
                    Iterator<String> keys = info.keys();
                    while (keys.hasNext()) {
                        cat1Id = keys.next();
                        JSONObject cat1 = info.getJSONObject(cat1Id);
                        cat1Name = cat1.getString("name");
                        JSONObject cat1Sub = cat1.getJSONObject("sub");
                        Iterator<String> cat2Keys = cat1Sub.keys();
                        while (cat2Keys.hasNext()) {
                            cat2Id = cat2Keys.next();
                            JSONObject cat2 = cat1Sub.getJSONObject(cat2Id);
                            cat2Name = cat2.getString("name");
                            JSONObject cat2Sub = cat2.getJSONObject("sub");
                            Iterator<String> cat3Keys = cat2Sub.keys();
                            while (cat3Keys.hasNext()) {
                                cat3Id = cat3Keys.next();
                                cat3Name = cat2Sub.getString(cat3Id);

                                ContentValues cv = new ContentValues();
                                cv.put(Beauty.Category.CATEGORY_ID, cat1Id);
                                cv.put(Beauty.Category.NAME, cat1Name);
                                cv.put(Beauty.Category.SUB_CATEGORY_ID, cat2Id);
                                cv.put(Beauty.Category.SUB_CATEGORY, cat2Name);
                                cv.put(Beauty.Category.SUB_SUB_CATEGORY_ID, cat3Id);
                                cv.put(Beauty.Category.SUB_SUB_CATEGORY, cat3Name);
                                Log.d("MainActivity", "put categories to db: " + cv.toString());
                                Uri uri = context.getContentResolver().insert(Beauty.Category.CONTENT_URI, cv);
                                if (uri != null) {
                                    Log.d("MainActivity", "inserted: " + uri);
                                } else {
                                    Log.d("MainActivity", "error inserting: " + cv);
                                }
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //内部类，获取function。。。完全不知道是什么东西
    private class GetFunctionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = URLConstant.FUNCTION_LIST_URL;
            Context context = MainActivity.this;
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONObject info = jsonObject.getJSONObject("info");
                    String cat1Id = "", cat2Id = "", cat1Name = "", cat2Name = "";
                    Iterator<String> keys = info.keys();
                    while (keys.hasNext()) {
                        cat1Id = keys.next();
                        JSONObject cat1 = info.getJSONObject(cat1Id);
                        cat1Name = cat1.getString("name");
                        JSONObject cat1Sub = cat1.getJSONObject("sub");
                        Iterator<String> cat2Keys = cat1Sub.keys();
                        while (cat2Keys.hasNext()) {
                            cat2Id = cat2Keys.next();
                            cat2Name = cat1Sub.getString(cat2Id);
                            ContentValues cv = new ContentValues();
                            cv.put(Beauty.Function.FUNCTION_ID, cat1Id);
                            cv.put(Beauty.Function.NAME, cat1Name);
                            cv.put(Beauty.Function.SUB_FUNCTION_ID, cat2Id);
                            cv.put(Beauty.Function.SUB_FUNCTION, cat2Name);
                            Log.d("MainActivity", "put categories to db: " + cv.toString());

                            Uri uri = context.getContentResolver().insert(Beauty.Function.CONTENT_URI, cv);
                            if (uri != null) {
                                Log.d("MainActivity", "inserted: " + uri);
                            } else {
                                Log.d("MainActivity", "error inserting: " + cv);
                            }

                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //按照字面意思是说得到城市地图，我估计是得到你的地址，然后给你相应的推荐
    private void getCityMap() {
        FileReader fr = null;
        BufferedReader bfr = null;
        try {
            File f = new File(Environment.getExternalStorageDirectory(), "area.csv");
            Log.d("MainActivity", "file is " + f.getAbsolutePath());
            if (f.exists()) {
                fr = new FileReader(f);
                bfr = new BufferedReader(fr);
                String line = bfr.readLine();
                while (line != null) {
                    String[] data = line.split(",");
                    ContentValues cv = new ContentValues();
                    cv.put(Beauty.Area.PROVINCE, data[0]);
                    cv.put(Beauty.Area.CITY, data[1]);
                    cv.put(Beauty.Area.DISTRICT, data[2]);
                    cv.put(Beauty.Area.PAY_AT_ARRIVAL, data[3].contentEquals("是") ? 1 : 0);
                    cv.put(Beauty.Area.POS, 0);
                    ;
                    Log.d("MainActivity", cv.toString());
                    getContentResolver().insert(Beauty.Area.CONTENT_URI, cv);
                    line = bfr.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bfr != null) { bfr.close(); }
                if (fr != null) { fr.close(); }
            } catch (Exception e) {

            }
        }
    }


}
