package com.maple.beautyjournal.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.ArticleDetailActivity;
import com.maple.beautyjournal.ProductDetailActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Recommend;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
   主页，继承BaseFragment 和OnClickListener


   先从本地加载，然后在从服务器上读取，这样的话比较好，给人一种错觉
 */
public class HomeFragment extends BaseFragment implements OnClickListener {

    CirclePageIndicator mPageControl;
    AdBaseAdapter mAdAdapter;
    ViewPager mAdGallery;
    ArrayList<Recommend> mLocalCacheRecommend = new ArrayList<Recommend>();   //本地缓存里的数据
    private final int[] deafultAds = {R.drawable.ad1, R.drawable.ad2, R.drawable.ad3, R.drawable.ad4, R.drawable.ad5};
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /*
    创建视图，用来回调返回一个视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View v = inflater.inflate(R.layout.activity_home, container, false);   //将layout加载成视图

        //initAds();    //初始化缓存
        initAdGallery(v);  //初始化滑动的那个东西

        ImageButton beauty = (ImageButton) v.findViewById(R.id.btn_beauty);
        beauty.setOnClickListener(this);

        ImageButton skin = (ImageButton) v.findViewById(R.id.btn_skin);
        skin.setOnClickListener(this);          //设置监听事件，监听点击事件

        ImageButton news = (ImageButton) v.findViewById(R.id.btn_news);
        news.setOnClickListener(this);

        ImageButton perfume = (ImageButton) v.findViewById(R.id.btn_perfume);
        perfume.setOnClickListener(this);

        ImageButton commodity = (ImageButton) v.findViewById(R.id.btn_commodity);
        commodity.setOnClickListener(this);
        new GetDataTask().execute();    //启动异步任务
        return v;   //返回视图
    }

    private void initAds() {
        loadLocalCache();
    }
    /*
    加载本地缓存，加载到gallery那下面的那行字体
     */
    private void loadLocalCache() {

        mLocalCacheRecommend.clear();
        String cache = SettingsUtil.getAdCache(context);    //从preferences里面取出
        if (!TextUtils.isEmpty(cache)) {
            try {
                JSONArray array = new JSONArray(cache);
                for (int i = 0; i < array.length(); i++) {
                    Recommend recommend = Recommend.fromJson(array.getJSONObject(i));
                    Log.d(TAG, "local cache: " + recommend.pic);
                    mLocalCacheRecommend.add(recommend);
                }
                return;
            } catch (Exception e) {
                //fallback to below section;
            }
        }
    }
    /*
    初始化Gallery，接收父容器
     */
    private void initAdGallery(View parent) {
        mAdGallery = (ViewPager) parent.findViewById(R.id.gallery);
        mAdAdapter = new AdBaseAdapter();  //初始化广告牌
        mAdGallery.setAdapter(mAdAdapter);
        mPageControl = (CirclePageIndicator) parent.findViewById(R.id.indicator);  //下面那个滑动的点点
        mPageControl.setViewPager(mAdGallery);
    }

    private static final int MAX_AD_COUNT = 5;

    /*
      PagerAdapter适配器类，适配数据
     */
    public class AdBaseAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            int ret = MAX_AD_COUNT;
            if (recommends.size() > 0) {
                ret = recommends.size();
            } else if (mLocalCacheRecommend.size() > 0) {
                ret = mLocalCacheRecommend.size();
            }
            return ret;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Recommend item = getItem(position);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.pager_item_layout, container, false);
            RoundAngleImageView v = (RoundAngleImageView)view.findViewById(R.id.image);
            TextView tv = (TextView)view.findViewById(R.id.pager_title);
            if (item != null) {
                ImageLoader.getInstance().displayImage(item.pic, v);
                tv.setText(item.title);
            } else {
                v.setImageDrawable(getAdDrawable(position));
                tv.setText("");
            }
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item == null || TextUtils.isEmpty(item.id)) {
                        return;
                    }
                    Intent intent;
                    if (item.type == Recommend.TYPE_ARTICLE) {
                        intent = new Intent(getActivity(), ArticleDetailActivity.class);
                        intent.putExtra(ArticleDetailActivity.ARTICLE_ID_EXTRA, item.id);
                    } else {
                        intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra(ProductDetailActivity.PRODUCT_ID, item.id);
                    }
                    startActivity(intent);
                }
            });
            container.addView(view);
            return view;
        }

        public Recommend getItem(int arg0) {
            if (recommends.size() > arg0) {
                return recommends.get(arg0);
            } else if (mLocalCacheRecommend.size() > arg0) {
                return mLocalCacheRecommend.get(arg0);
            }
            return null;
        }

    }

    private Drawable getAdDrawable(int position) {
        return getResources().getDrawable(deafultAds[position%5]);
    }

    /*
       当点击分类页面的时候会触发相应的事件
       然后会跳转到相应的页面
     */
    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        Bundle bundle = new Bundle();
        if (id == R.id.btn_beauty) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            bundle.putString("key", "beauty");    //传递key=beauty
            Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

            ft.replace(R.id.realtabcontent, article_list);
            ft.addToBackStack(null);
            ft.commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();

        } else if (id == R.id.btn_skin) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();


            bundle.putString("key", "skin_protect");
            Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

            ft.replace(R.id.realtabcontent, article_list);
            ft.addToBackStack(null);
            ft.commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();

        } else if (id == R.id.btn_perfume) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            bundle.putString("key", "perfume");
            Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

            ft.replace(R.id.realtabcontent, article_list);
            ft.addToBackStack(null);
            ft.commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();

        } else if (id == R.id.btn_news) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            bundle.putString("key", "news");
            Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

            ft.replace(R.id.realtabcontent, article_list);
            ft.addToBackStack(null);
            ft.commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();

        } else if (id == R.id.btn_commodity) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment product_cate = Fragment.instantiate(getActivity(), ProductCategoryFragment.class.getName(), null);

            ft.replace(R.id.realtabcontent, product_cate);
            ft.addToBackStack(null);
            ft.commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();
        }
        getActivity().findViewById(R.id.bottom).setVisibility(View.GONE);

    }

    private List<Recommend> recommends = new ArrayList<Recommend>();

    /*
    定义一个异步执行的类来执行相应的获取数据的操作
     */
    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url = NetUtil.getAdUrl(context, MAX_AD_COUNT);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "getDataTask: " + result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    recommends.clear();
                    JSONArray array = obj.getJSONArray("info");
                    SettingsUtil.saveAdCache(context, array.toString());   //保存在缓存中
                    for (int i = 0; i < array.length(); i++) {
                        Recommend item = Recommend.fromJson(array.getJSONObject(i));
                        Log.d(TAG, "add recommend: " + item.pic + ", " + item.title);
                        recommends.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdGallery.setAdapter(mAdAdapter);
            mAdAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }

}
