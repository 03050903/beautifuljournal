package com.maple.beautyjournal.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.ArticleDetailActivity;
import com.maple.beautyjournal.LoginActivity;
import com.maple.beautyjournal.ProductDetailActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Article;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends BaseFragment implements OnClickListener {
    private final int TAB_ARTICLE = 0;
    private final int TAB_PRODUCT = 1;
    private int currentTab;

    private FrameLayout tab_article;
    private FrameLayout tab_product;

    PullToRefreshListView list1;
    TextView mList1_footerView = null;
    PullToRefreshListView list2;
    TextView mList2_footerView = null;
    View contentView;
    List<Product> products = new ArrayList<Product>();
    List<Article> articles = new ArrayList<Article>();
    FavArticleAdapter articleAdapter;
    FavProductAdapter productAdapter;
    View content_parent;
    View no_login;
    private Context context;
    
    private TextView  mEditStatusTextView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_favorite, container, false);   //将layout布局装载成一个View对象
        context = getActivity();
        contentView = v.findViewById(R.id.content);   //获得内容视图
        content_parent = v.findViewById(R.id.content_parent);   //获得content_parent视图
        no_login = v.findViewById(R.id.no_login);
        //定义还没有收藏文章TextView
        mList1_footerView = new TextView(context);
        mList1_footerView.setGravity(Gravity.CENTER);
        mList1_footerView.setHeight(100);
        mList1_footerView.setText(R.string.no_fav_article);
        //定义还没有收藏商品TextView
        mList2_footerView = new TextView(context);
        mList2_footerView.setGravity(Gravity.CENTER);
        mList2_footerView.setHeight(100);
        mList2_footerView.setText(R.string.no_fav_product);

        Button quick_login = (Button) v.findViewById(R.id.quick_login);
        quick_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //如果没有登录的话就启动登录的Activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            }

        });
        initTab(v);
        initList1(v);
        initList2(v);
        currentTab = TAB_ARTICLE;
        //如果没有登录的话，询问是否先登录
        if (!SettingsUtil.isLoggedIn(getActivity())) {
            no_login.setVisibility(View.VISIBLE);
            content_parent.setVisibility(View.GONE);  //将视图隐藏起来
        }
        
        mEditStatusTextView = (TextView) v.findViewById(R.id.tv_action);
        mEditStatusTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        String manage = getResources().getString(R.string.management);
		        String done = getResources().getString(R.string.done);				
				if(manage.equals(mEditStatusTextView.getText())){
					mEditStatusTextView.setText(done);
					articleAdapter.setIsEditMode(true);
					productAdapter.setIsEditMode(true);
				}else{
					mEditStatusTextView.setText(manage);					
					articleAdapter.setIsEditMode(false);
					productAdapter.setIsEditMode(false);
				}
				articleAdapter.notifyDataSetChanged();
				productAdapter.notifyDataSetChanged();
			}
		});        
        return v;
    }

    private void initTab(View parent) {
        tab_article = (FrameLayout) parent.findViewById(R.id.tab_article);
        tab_article.setOnClickListener(this);
        tab_product = (FrameLayout) parent.findViewById(R.id.tab_product);
        tab_product.setOnClickListener(this);

    }

    private void initList1(View parent) {
        list1 = (PullToRefreshListView) parent.findViewById(R.id.list1);
        list1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new GetDataTask(TAB_ARTICLE).execute();
            }
        });
        articleAdapter = new FavArticleAdapter();
        list1.setAdapter(articleAdapter);
    }

    private void initList2(View parent) {
        list2 = (PullToRefreshListView) parent.findViewById(R.id.list2);
        list2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new GetDataTask(TAB_PRODUCT).execute();
            }
        });
        productAdapter = new FavProductAdapter();
        list2.setAdapter(productAdapter);
    }

    private void setTab(int tab) {
        currentTab = tab;

        switch (currentTab) {
            case TAB_ARTICLE:
                tab_article.getChildAt(1).setVisibility(View.VISIBLE);
                tab_product.getChildAt(1).setVisibility(View.GONE);
                list1.setVisibility(View.VISIBLE);
                list2.setVisibility(View.GONE);
                if (articles.size() == 0 && SettingsUtil.isLoggedIn(getActivity())) {
                	new GetDataTask(tab).execute(); 
                	}else{
                		list1.onRefreshComplete();
                	}
                break;
            case TAB_PRODUCT:
                tab_article.getChildAt(1).setVisibility(View.GONE);
                tab_product.getChildAt(1).setVisibility(View.VISIBLE);
                list1.setVisibility(View.GONE);
                list2.setVisibility(View.VISIBLE);
                if (products.size() == 0 && SettingsUtil.isLoggedIn(getActivity())) {
                	new GetDataTask(tab).execute(); 
                	}else{
                		list2.onRefreshComplete();
                	}
                break;
        }
    }

    //点击事件
    @Override
    public void onClick(View arg0) {
        if (arg0 == tab_article) {
            setTab(TAB_ARTICLE);
        } else if (arg0 == tab_product) {
            setTab(TAB_PRODUCT);
        }
    }

    private String errorMsg;
    private static final String TAG = "FavoriteActivity";

   //异步任务，获取数据，收藏的文章那种形式
    private class GetDataTask extends AsyncTask<Void, Void, Void> {
    	private int mCurrentTab;
    	public GetDataTask(int curTab ){
    		mCurrentTab = curTab;
    	}
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            showProgress();
            switch(mCurrentTab){
            case TAB_ARTICLE:
                list1.getRefreshableView().removeFooterView(mList1_footerView);
                break;
            case TAB_PRODUCT:
                list2.getRefreshableView().removeFooterView(mList2_footerView);
                break;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = null;
            Context context = getActivity();
            switch (mCurrentTab) {
                case TAB_ARTICLE:
                    url = NetUtil.getFavArticleListUrl(context);
                    break;
                case TAB_PRODUCT:
                    url = NetUtil.getFavProductListUrl(context);
                    break;
            }
            if (TextUtils.isEmpty(url)) {
                return null;
            }
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "result is " + result);
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    JSONArray array = json.optJSONArray("info");
                    if (array != null && array.length() > 0) {
                        switch (currentTab) {
                            case TAB_ARTICLE:
                                articles.clear();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    articles.add(ServerDataUtils.getArticleFromJSONObject(obj));
                                }
                                break;
                            case TAB_PRODUCT:
                                products.clear();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    products.add(ServerDataUtils.getProductFromJSONObject(obj));
                                }
                                break;
                        }
                    } else {
                        switch (currentTab) {
                            case TAB_ARTICLE:
                                errorMsg = getString(R.string.no_fav_article);
                                break;
                            case TAB_PRODUCT:
                                errorMsg = getString(R.string.no_fav_product);
                                break;
                        }
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
            articleAdapter.notifyDataSetChanged();
            productAdapter.notifyDataSetChanged();
            if (!TextUtils.isEmpty(errorMsg)) {
                switch(mCurrentTab){
                case TAB_ARTICLE:
                	mList1_footerView.setText(errorMsg);
                	mList1_footerView.setWidth(list1.getWidth());
                	Log.e("list1 height", ""+list1.getHeight());
                    list1.getRefreshableView().addFooterView(mList1_footerView);
                    break;
                case TAB_PRODUCT:
                	mList2_footerView.setText(errorMsg);
                	mList2_footerView.setWidth(list2.getWidth());
                    list2.getRefreshableView().addFooterView(mList2_footerView);
                    break;
                }
                errorMsg = null;
            }
            switch (mCurrentTab) {
                case TAB_PRODUCT:
                    list2.onRefreshComplete();
                    break;
                case TAB_ARTICLE:
                    list1.onRefreshComplete();
                    break;
            }
            super.onPostExecute(aVoid);
        }

    }

    private class FavProductAdapter extends BaseAdapter {

    	private boolean mIsEditMode = false;

		public void setIsEditMode(boolean isEditMode) {
			this.mIsEditMode = isEditMode;
		}

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int i) {
            return products.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.fav_product_list_item, parent, false);
            }
            final Product product = products.get(position);
            TextView tv1 = (TextView) v.findViewById(R.id.line1Text);
            tv1.setText(product.name);
            TextView price = (TextView) v.findViewById(R.id.price);
            price.setText("￥" + product.price);
            ImageView iv = (ImageView) v.findViewById(R.id.image);
            if (!TextUtils.isEmpty(product.pic)) {                
                if (product.pic.startsWith("http")) {
                    ImageLoader.getInstance().displayImage(product.pic, iv);
                } else {
                    ImageLoader.getInstance().displayImage(URLConstant.SERVER_ADDRESS + product.pic, iv);
                }

            } else {
                iv.setImageResource(R.drawable.default_product);
            }
            ImageView addKartOrdeleteProd = (ImageView) v.findViewById(R.id.addKart_or_deleteProd);
            if(!this.mIsEditMode){
            	addKartOrdeleteProd.setImageResource(R.drawable.add_kart_image);
                addKartOrdeleteProd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsUtil.addProductToKart(getActivity(), product);
                        Toast.makeText(getActivity(), getString(R.string.added_to_kart), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
            	addKartOrdeleteProd.setImageResource(R.drawable.button_delete);
            	addKartOrdeleteProd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.confirm_delete_favorite)
                                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new CancelFavArticleTask(product.id, currentTab).execute();
                                    }
                                }).setNegativeButton(getString(android.R.string.cancel), null).show();
                    }
                });
            }
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                    intent.putExtra(ProductDetailActivity.PRODUCT_ID, product.id);
                    startActivity(intent);
                }
            });
            RatingBar rating = (RatingBar) v.findViewById(R.id.rate);
            if (product.star == 0) { product.star = 3; }
            rating.setNumStars(product.star);
            return v;
        }
    }

    private class FavArticleAdapter extends BaseAdapter {

    	private boolean mIsEditMode = false;

		public void setIsEditMode(boolean isEditMode) {
			this.mIsEditMode = isEditMode;
		}

        @Override
        public int getCount() {
            return articles.size();
        }

        @Override
        public Object getItem(int i) {
            return articles.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(getActivity()).inflate(R.layout.favorite_list_item_article, parent, false);
            }
            final Article article = articles.get(position);
            TextView tv = (TextView) v.findViewById(R.id.title);
            tv.setText(article.title);
            v.setTag(article);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                    intent.putExtra(ArticleDetailActivity.ARTICLE_ID_EXTRA, article.id);
                    startActivity(intent);
                }
            });
            
            ImageView delete = (ImageView) v.findViewById(R.id.delete);
            if(mIsEditMode){
            	delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.confirm_delete_favorite)
                                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        new CancelFavArticleTask(article.id, TAB_ARTICLE).execute();
                                    }
                                }).setNegativeButton(getString(android.R.string.cancel), null).show();
                    }
                });            	
            }else{
            	delete.setVisibility(View.GONE);            	
            }
            return v;
        }
    }

    //异步任务，取消收藏文章时所开启的异步任务
    private class CancelFavArticleTask extends AsyncTask<Void, Void, Void> {
        private String itemId;
        private int type;

        CancelFavArticleTask(String itemId, int type) {
            this.itemId = itemId;
            this.type = type;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String url = (type == TAB_ARTICLE) ? NetUtil.getCancelFavArticleUrl(context, itemId) : NetUtil
                    .getCancelFavProductUrl(context, itemId);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "FavTask, result is " + result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    errorMsg = null;
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(obj);
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgress();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            showProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            } else {
                if (type == TAB_ARTICLE) {
                    for (Article article : articles) {
                        if (itemId.contentEquals(article.id)) {
                            articles.remove(article);
                            break;
                        }
                    }
                    articleAdapter.notifyDataSetChanged();
                } else {
                    for (Product product : products) {
                        if (itemId.contentEquals(product.id)) {
                            products.remove(product);
                            break;
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                }
            }
        }
    }
    

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SettingsUtil.isLoggedIn(getActivity())) {
            setTab(currentTab);
            no_login.setVisibility(View.GONE);
            content_parent.setVisibility(View.VISIBLE);
        } else {
            products.clear();
            articles.clear();
            setTab(currentTab);
            no_login.setVisibility(View.VISIBLE);
            content_parent.setVisibility(View.GONE);
        }
    }

}
