package com.maple.beautyjournal.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.ArticleDetailActivity;
import com.maple.beautyjournal.MainActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.adapter.ArticlePagerAdapter;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Article;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
文章列表页，美妆，护肤，商品，香水，资讯，都会跳转到这里来
 */
public class ArticleListFragment extends BaseFragment implements OnPageChangeListener {

   // private ArrayList<MenuItem> menu = new ArrayList<MenuItem>();  //底下菜单
    private int mMenuItemHeight;
    private int page = 1;
    private int category = 101;         //类别
   // private Map<String, MenuItem> sCategoryMap = new HashMap<String, MenuItem>();
    private List<List<Article>> articles = new ArrayList<List<Article>>();
    private ViewPager viewPager;
    private TextView mArticleListMessageView;
    TextView mCateSwitcher;
    String key;
    //PagerAdapter adapter ;
    TextView pageCount;
    private RadioGroup radioGroup;
    private int currentIndicatorLeft = 0;

    private FragmentPagerAdapter mAdapter;
    // 页卡内容
    private ViewPager mPager;
    // Tab页面列表
    private List<View> listViews;
    // 当前页卡编号
   // private LocalActivityManager manager = null;

    //private MyPagerAdapter mpAdapter = null;
    private int index;


    /*
        做一些初始化的工作，初始化成员变量
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuItemHeight = Utils.dip2px(this.getActivity(), 40);
        /*
        sCategoryMap.put("beauty", new MenuItem(getString(R.string.menu_beauty_item1), 101));
        sCategoryMap.put("skin_protect", new MenuItem(getString(R.string.menu_skin_item1), 201));
        sCategoryMap.put("perfume", new MenuItem(getString(R.string.menu_perfume_item1), 301));
        sCategoryMap.put("news", new MenuItem(getString(R.string.menu_brand_item1), 401));

        Bundle bundle = getArguments();   //获得bundle数据，
        key = bundle.getString("key");
        if (key.equals("beauty")) {
            menu.add(new MenuItem(getString(R.string.menu_beauty_item1), 101));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item2), 102));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item3), 103));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item4), 104));
            menu.add(new MenuItem(getString(R.string.menu_beauty_item5), 105));
        } else if (key.equals("skin_protect")) {
            menu.add(new MenuItem(getString(R.string.menu_skin_item1), 201));
            menu.add(new MenuItem(getString(R.string.menu_skin_item2), 202));
            menu.add(new MenuItem(getString(R.string.menu_skin_item3), 203));
            menu.add(new MenuItem(getString(R.string.menu_skin_item4), 204));

        } else if (key.equals("perfume")) {
            menu.add(new MenuItem(getString(R.string.menu_perfume_item1), 301));
            menu.add(new MenuItem(getString(R.string.menu_perfume_item2), 302));

        } else if (key.equals("news")) {
            menu.add(new MenuItem(getString(R.string.menu_brand_item1), 401));
            menu.add(new MenuItem(getString(R.string.menu_brand_item2), 402));
        }
        category = sCategoryMap.get(key).id;  //指代现在是什么类别
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_article_list, container, false);
        pageCount = (TextView) v.findViewById(R.id.page_count);
        TextView tv_article_list_title = (TextView)v.findViewById(R.id.tv_article_list_title);
        tv_article_list_title.setText("美妆");
        initBack(v);    //初始化返回按钮
       // initCategorySwitcher(v);
        viewPager = (ViewPager) v.findViewById(R.id.article_list_viewpager);
        radioGroup = (RadioGroup)v.findViewById(R.id.radio_group_switcher) ;
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        mArticleListMessageView = (TextView) v.findViewById(R.id.articleListEmptyTextView);
        viewPager.setOnPageChangeListener(this);
        adapter = new ArticlePagerAdapter(this.getActivity());
        viewPager.setAdapter(adapter);
        OnArticleItemClickListener onArticleItemClickListener = new OnArticleItemClickListener();
        adapter.setOnArticleItemClickListener(onArticleItemClickListener);
        new GetDataTask().execute();
        return v;
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if (radioGroup.getChildAt(i) != null){
                /*
                TranslateAnimation animation = new TranslateAnimation(
                        currentIndicatorLeft , ((RadioButton)radioGroup.getChildAt(i)).getLeft() ,0 , 0);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(100);
                animation.setFillAfter(true);
                radioGroup.startAnimation(animation);
                viewPager.setCurrentItem(i);
                currentIndicatorLeft = ((RadioButton) radioGroup.getChildAt(i)).getLeft();
                syncHorizontalScrollView.smoothScrollTo(
                        (i > 1 ? ((RadioButton) radioGroup.getChildAt(i)).getLeft() : 0) - ((RadioButton) radioGroup.getChildAt(2)).getLeft(), 0);
                */
                viewPager.setCurrentItem(i);
            }
        }
    };

    private void initBack(View parent) {
        ImageButton btn_back = (ImageButton) parent.findViewById(R.id.img_btn_back);
        btn_back.setBackgroundResource(R.drawable.left_arrow_2);
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                startActivity(new Intent().setClass(getActivity(), MainActivity.class));
            }

        });
    }

    /*
    private void initCategorySwitcher(View parent) {
        mCateSwitcher = (TextView) parent.findViewById(R.id.btn_cate);
        //mCateSwitcher.setText(sCategoryMap.get(key).title);
        mCateSwitcher.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                createMenu(menu).showAsDropDown(arg0, Utils.dip2px(ArticleListFragment.this.getActivity(),-15), Utils
                        .dip2px(ArticleListFragment.this.getActivity(), 8));

            }

        });
    }
*/
    /*
    private PopupWindow createMenu(ArrayList<MenuItem> menus) {
        Context context = this.getActivity();
        final PopupWindow window = new PopupWindow(context);
        ListView list = new ListView(context);
        list.setLayoutParams(new android.view.ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                                                                     LayoutParams.WRAP_CONTENT));
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MenuItem item = (MenuItem) arg0.getItemAtPosition(arg2);
                mCateSwitcher.setText(item.title);
                category = item.id;
                new GetDataTask().execute();
                window.dismiss();
            }

        });
        MenuAdapter adapter = new MenuAdapter(context, menus);
//        list.setDivider(context.getResources().getDrawable(R.drawable.list_divider));
        list.setSelector(context.getResources().getDrawable(R.drawable.menu_selected));
        list.setAdapter(adapter);
        list.setVerticalScrollBarEnabled(false);
        window.setContentView(list);
        window.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.menu_bg));
        window.setWidth(Utils.dip2px(context, 130));
        window.setHeight(mMenuItemHeight * menus.size() + 4);
        // 设置PopupWindow外部区域是否可触摸
        window.setFocusable(true); // 设置PopupWindow可获得焦点
        window.setTouchable(true); // 设置PopupWindow可触摸
        window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        return window;
    }

    public class MenuAdapter extends BaseAdapter {

        ArrayList<MenuItem> items;
        Context context;

        public MenuAdapter(Context c, ArrayList<MenuItem> items) {
            context = c;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int arg0) {
            return items.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            TextView v;
            if (arg1 == null) {
                v = new TextView(context);
                v.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mMenuItemHeight));
                v.setGravity(Gravity.CENTER);
                v.setTextSize(16);
            } else {
                v = (TextView) arg1;
            }
            MenuItem item = items.get(arg0);
            v.setText(item.title);
            return v;
        }

    }

    public class MenuItem {
        public String title;
        public int id;

        public MenuItem(String title, int id) {
            this.title = title;
            this.id = id;
        }
    }

*/
    private static String errorMsg;
    private static final int DEFAULT_SIZE1 = 6;
    private static final int DEFAULT_SIZE2 = 10;

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Context context = ArticleListFragment.this.getActivity();
            int size = DEFAULT_SIZE1;
            String url = NetUtil.getArticleListUrl2(context, category, page, size);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "result is " + result);
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    articles.clear();
                    JSONArray array = json.getJSONArray("info");
                    Log.d(TAG, "get " + array.length() + " items");
                    for (int i = 0; i < array.length(); i++) {
                        ArrayList<Article> articleList = new ArrayList<Article>();
                        JSONArray articleArray = array.getJSONArray(i);
                        for (int j = 0; j < articleArray.length(); j++) {
                            JSONObject obj = articleArray.getJSONObject(j);
                            Article article = ServerDataUtils.getArticleFromJSONObject(obj);
                            if(article == null || article.id == null){
                            	continue;
                            }
                            if (!TextUtils.isEmpty(article.pic)) {
                                article.pic = URLConstant.SERVER_ADDRESS + article.pic;
                            }
                            Log.d(TAG, "get article : " + article.title);
                            articleList.add(article);
                        }
                        Log.d(TAG, "get a line of article: " + articleList.size());
                        articles.add(articleList);
                        adapter.setArticles(articles);
                    }
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(json);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
            	//mArticleListMessageView.setVisibility(View.VISIBLE);
                //mArticleListMessageView.setText(errorMsg);
                errorMsg = null;
            } else if (articles.size() == 0) {
                //empty, show prompt
               // mArticleListMessageView.setVisibility(View.VISIBLE);
               // mArticleListMessageView.setText(R.string.empty_article_list);
            } else {
            	viewPager.setCurrentItem(0);
            	viewPager.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
            viewPager.setCurrentItem(0);
            viewPager.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        	viewPager.setVisibility(View.GONE);
            mArticleListMessageView.setVisibility(View.GONE);
            dismissProgress();
            showProgress();
        }
    }

    private ArticlePagerAdapter adapter;

    private class OnArticleItemClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            Article article = (Article) view.getTag();
            Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.ARTICLE_ID_EXTRA, article.id);
            startActivity(intent);

        }
    }

    void showPageCount() {
        pageCount.setVisibility(View.VISIBLE);
        if (articles != null) {
            pageCount.setText(viewPager.getCurrentItem() + 1 + "/" + articles.size());
        }
//        pageCount.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade));
    }

    void dismissPageCount() {

        Animation ani = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);
        ani.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                pageCount.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

        });
        pageCount.startAnimation(ani);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

        if (arg0 == 1) {
            showPageCount();
        } else if (arg0 == 0) {
            dismissPageCount();
        }

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {


    }

    @Override
    public void onPageSelected(int arg0) {
        if (radioGroup != null && radioGroup.getChildCount() > arg0){
            ((RadioButton)radioGroup.getChildAt(arg0)).performClick() ;
        }

    }

}
