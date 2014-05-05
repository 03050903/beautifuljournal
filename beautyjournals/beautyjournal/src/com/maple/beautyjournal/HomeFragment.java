package com.maple.beautyjournal;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.entitiy.Recommend;
import com.maple.beautyjournal.fragment.ArticleListFragment;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mosl on 14-4-10.
 */
public class HomeFragment extends Fragment {

    private static final String TAG="HomeFragment";
    CirclePageIndicator mPageControl;
    AbPageAdapter mAdAdapter;
    ViewPager mAdGallery;
    private Context context;
    private ImageView btn_beauty,btn_skin,btn_perfume,btn_news;
    private GridView hotWordsGridView;
    private ArrayList<String> localhotWords=new ArrayList<String>();
    private ArrayList<String> remotehotWords=new ArrayList<String>();
    private HotWordsAdapter hotWordsAdapter;
    LinearLayout bottomLinearLayout = null ;
    RelativeLayout titleLayout = null ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        context=getActivity();
        View view = inflater.inflate(R.layout.activity_home_content,container,false);
        bottomLinearLayout = (LinearLayout)getActivity().findViewById(R.id.bottom) ;
        titleLayout = (RelativeLayout)getActivity().findViewById(R.id.title_layout) ;
        getActivity().findViewById(R.id.bottom).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.title_layout).setVisibility(View.VISIBLE);
        initAds();
        initHotWords(view);
        initAdGallery(view);
        initCategoryBtn(view);
        new GetDataTask().execute();    //启动异步任务
        new GetHotWordsTask().execute();
        return view;
    }

    private void initHotWords(View view){
        hotWordsGridView=(GridView)view.findViewById(R.id.hotWords_grview);
        String cache = SettingsUtil.getHotWordsCache(context);
        localhotWords.clear();
        if (!TextUtils.isEmpty(cache)) {
            try {
                JSONArray array = new JSONArray(cache);
                for (int i = 0; i < array.length(); i++) {
                    localhotWords.add(array.get(i).toString());
                }
            } catch (Exception e) {

            }
        }
        Log.d("XXX","-------local"+localhotWords.toString());
        hotWordsAdapter=new HotWordsAdapter(context,localhotWords);
        hotWordsGridView.setAdapter(hotWordsAdapter);
        hotWordsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView=(TextView)view.findViewById(R.id.hot_words_item);
                String itemStr=textView.getText().toString();
                Bundle bundle=new Bundle();
                bundle.putString("search",itemStr);
                Intent search=new Intent();
                search.putExtra("key",bundle);
                startActivity(search.setClass(getActivity(),SearchActivity.class));
            }
        });
    }
    private class HotWordsAdapter extends BaseAdapter{

        private List<String> hotWords=new ArrayList<String>();
        private LayoutInflater inflater;
        public HotWordsAdapter(Context context,List<String> hotWords){
            inflater= LayoutInflater.from(context);
            this.hotWords=hotWords;
        }
        @Override
        public int getCount() {

            return hotWords.size();
        }

        @Override
        public Object getItem(int position) {
            return hotWords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView hot=null;
            if(convertView==null){
                convertView= inflater.inflate(R.layout.activity_home_2_hotwords, parent, false);
                hot=(TextView)convertView.findViewById(R.id.hot_words_item);
                hot.setText(hotWords.get(position));
            }
           return convertView;
        }
    }
    private void initCategoryBtn(View view){

        btn_beauty=(ImageView)view.findViewById(R.id.btn_beauty);
        btn_perfume=(ImageView)view.findViewById(R.id.btn_perfume);
        btn_skin=(ImageView)view.findViewById(R.id.btn_skin);
        btn_news=(ImageView)view.findViewById(R.id.btn_news);

        btn_beauty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Bundle bundle = new Bundle();
                //bundle.putString("key", "btn_beauty");    //传递key=beauty
                Intent intent=new Intent();
                intent.putExtras(bundle);
                //intent.setClass(context,ArticleDetailTwoActivity.class);
                //startActivity(intent);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                bundle.putString("key", "beauty");    //传递key=beauty
                Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

                ft.replace(R.id.content, article_list);
                ft.addToBackStack(null);
                bottomLinearLayout.setVisibility(View.GONE);
                titleLayout.setVisibility(View.GONE);
                ft.commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
                return false;
            }
        });
        btn_skin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Bundle bundle = new Bundle();
                //bundle.putString("key", "skin_protect");    //传递key=beauty
                Intent intent=new Intent();
                intent.putExtras(bundle);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                bundle.putString("key", "skin_protect");    //传递key=beauty
                Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

                ft.replace(R.id.content, article_list);
                ft.addToBackStack(null);
                bottomLinearLayout.setVisibility(View.GONE);
                titleLayout.setVisibility(View.GONE);
                ft.commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
                return false;
            }
        });
        btn_perfume.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Bundle bundle = new Bundle();
                //bundle.putString("key", "prefume");    //传递key=beauty
                Intent intent=new Intent();
                intent.putExtras(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                bundle.putString("key", "perfume");    //传递key=beauty
                Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

                ft.replace(R.id.content, article_list);
                ft.addToBackStack(null);
                bottomLinearLayout.setVisibility(View.GONE);
                titleLayout.setVisibility(View.GONE);
                ft.commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
                return false;
            }
        });
        btn_news.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Bundle bundle = new Bundle();
                //bundle.putString("key", "news");    //传递key=beauty
                Intent intent=new Intent();
                intent.putExtras(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                bundle.putString("key", "news");    //传递key=beauty
                Fragment article_list = Fragment.instantiate(getActivity(), ArticleListFragment.class.getName(), bundle);

                ft.replace(R.id.content, article_list);
                ft.addToBackStack(null);
                bottomLinearLayout.setVisibility(View.GONE);
                titleLayout.setVisibility(View.GONE);
                ft.commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
                return false;
            }
        });
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
                    Log.d("XXX",recommend.title);
                }
                return;
            } catch (Exception e) {
            }
        }
    }
    private void initAdGallery(View parent) {
        mAdGallery = (ViewPager) parent.findViewById(R.id.gallery);
        mAdAdapter = new AbPageAdapter();  //初始化广告牌
        mAdGallery.setAdapter(mAdAdapter);
        mPageControl = (CirclePageIndicator) parent.findViewById(R.id.indicator);  //下面那个滑动的点点
        mPageControl.setViewPager(mAdGallery);
    }

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
                    Log.d("XXX",array.toString());
                    for (int i = 0; i < array.length(); i++) {
                        Recommend item = Recommend.fromJson(array.getJSONObject(i));
                        recommends.add(item);
                    }
                    mAdAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class GetHotWordsTask extends AsyncTask<Void, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            String url = NetUtil.getHotWords(context, MAX_AD_COUNT);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d("XXX",result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    remotehotWords.clear();
                    JSONArray array = obj.getJSONArray("info");
                    Log.d("XXX",array.toString());
                    SettingsUtil.saveHotWordsCache(context, array.toString());   //保存在缓存中
                    for (int i = 0; i < array.length(); i++) {
                        remotehotWords.add(array.get(i).toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }


    private static final int MAX_AD_COUNT = 5;
    private List<Recommend> recommends = new ArrayList<Recommend>();     //下载下来的
    ArrayList<Recommend> mLocalCacheRecommend = new ArrayList<Recommend>();   //本地缓存里的数据
    private final int[] deafultAds = {R.drawable.ad1, R.drawable.ad2, R.drawable.ad3, R.drawable.ad4, R.drawable.ad5};


    public class AbPageAdapter extends PagerAdapter{

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
            return view==o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Recommend item = getItem(position);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.pager_item_layout, container, false);
            RoundAngleImageView v = (RoundAngleImageView)view.findViewById(R.id.image);
            TextView tv = (TextView)view.findViewById(R.id.pager_title);
            if (item != null) {
                ImageLoader.getInstance().displayImage(item.pic, v);
                tv.setText(item.title);
                Log.d("XXX",item.title);
            } else {
                v.setImageDrawable(getAdDrawable(position));
                tv.setText("");
            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item == null || TextUtils.isEmpty(item.id)) {
                        return;
                    }
                    Intent intent;
                    if (item.type == Recommend.TYPE_ARTICLE) {
                        intent = new Intent(context, ArticleDetailActivity.class);   //跳转到文章详情页
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

        private Drawable getAdDrawable(int position) {
            return getResources().getDrawable(deafultAds[position%5]);
        }
    }



}