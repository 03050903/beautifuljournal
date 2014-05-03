package com.maple.beautyjournal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kii.wine.components.gallery.Gallery;

/**
 * Created by tian on 13-6-14.
 */
public class ArticleDetailActivity extends BaseActivity implements View.OnClickListener {
    private WebView webView;
    private String errorMsg;
    private String itemId;
    public static final String ARTICLE_ID_EXTRA = "com.maple.beautyjournal.itemId";
    private String barTitle;
    private String content;
    private TextView likeView;
    private String likeCount;
    private ArrayList<Product> mRelatedProducts = new ArrayList<Product>();
    Gallery mRelatedProGallery;
    private RelatedProductAdapter adapter;
    private TextView titleView;
    private View contentView;
    private Activity context;
    private ImageButton favButton;
    private boolean isFavorite = false;
    private View expend;
    private View mNotFoundView;
    private ImageView article_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        contentView = findViewById(R.id.content);
        initExpandButton();
        initFavButton();
        mRelatedProGallery = (Gallery) findViewById(R.id.gallery);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JavascriptInterface(this), "imageListener");
        likeView = (TextView) findViewById(R.id.like);
        likeView.setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.title);
        itemId = getIntent().getStringExtra(ARTICLE_ID_EXTRA);
        adapter = new RelatedProductAdapter();
        mRelatedProGallery.setAdapter(adapter);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                new GetDataTask().execute();
            }
        });
        context = this;
        article_comment=(ImageView)findViewById(R.id.article_comment);
        article_comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Bundle bundle=new Bundle();
                bundle.putString("articleId",itemId);
                startActivity(new Intent(ArticleDetailActivity.this,ArticleCommentActivity.class).putExtras(bundle));
                return false;
            }
        });
        this.mNotFoundView = findViewById(R.id.article_not_found_view);
    }

    private void addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){" +
                                "var objs = document.getElementsByTagName(\"a\"); " +
                                "for(var i=0;i<objs.length;i++)  " +
                                "{" + "    objs[i].onclick=function()  " +
                                "    {  " + "        window.imageListener.openUrl(this.href);  " +
                                "    }  " +
                                "}" +
                                "})()");
    }

    private synchronized void resetRelated_productLayout(boolean fromTop){
		if(fromTop == this.mIsToUp){
	    	Log.e(TAG, "0");
	        View mRelated_product_layout = findViewById(R.id.related_product_layout);                
	        ImageView mImageArrow = (ImageView) findViewById(R.id.imageView_arrow);
	        if (mRelated_product_layout.getLayoutParams().height == 0) {
	        	mRelated_product_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
	                                                            LayoutParams.WRAP_CONTENT));
	        	mImageArrow.setImageResource(R.drawable.arrowdown);
	        	this.mIsToUp = false;
	        } else {
	        	mRelated_product_layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
	        	mImageArrow.setImageResource(R.drawable.arrowup);
	        	this.mIsToUp = true;
	        }
		}
    }

    private float mMotionY=0;
    private boolean mIsToUp = true;
	private void initExpandButton() {
        expend =  findViewById(R.id.expand);
        expend.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					mMotionY = event.getY();
				}else if(event.getAction() == MotionEvent.ACTION_MOVE){
					float mDetaMotionY = mMotionY - event.getY();
					if(mDetaMotionY>5 && mDetaMotionY<20 && mIsToUp ){
						Log.e(TAG, "up");
						resetRelated_productLayout(mIsToUp);
						return true;
					}else if( mDetaMotionY<-5 && mDetaMotionY>-20 && !mIsToUp ){				
						Log.e(TAG, "down");
						resetRelated_productLayout(mIsToUp);
						return true;
					}
				}
				return false;
			}
		});
        expend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
            	resetRelated_productLayout(mIsToUp);
            }
        });
    }

    private void initFavButton() {

        favButton = (ImageButton) findViewById(R.id.favo);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SettingsUtil.isLoggedIn(context)) {
                    new FavTask().execute();
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    Toast.makeText(context, getString(R.string.pls_log_in_first), Toast.LENGTH_SHORT).show();
                    startActivity(intent);  //启动登陆界面
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.like:
                new LikeTask().execute();
                break;
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String url = NetUtil.getArticleDetailUrl(context, itemId);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    JSONObject info = json.getJSONObject("info");
                    barTitle = info.optString("item_title");
                    content = (String) info.optString("item_content");
                    likeCount = info.optString("item_like");
                    int item_favorite = info.optInt("item_favorite");
                    if (item_favorite == 1) {
                        isFavorite = true;
                    }
                    Log.d(TAG, "info is " + info);
                    errorMsg = null;
                    JSONArray array = info.optJSONArray("item_product");
                    if (array != null) {
                        mRelatedProducts.clear();
                        for (int i = 0; i < array.length(); i++) {
                            Product product = new Product();
                            JSONObject productObject = array.getJSONObject(i);
                            product.id = productObject.getString("id");
                            product.name = productObject.getString("name");
                            product.pic = URLConstant.SERVER_ADDRESS + productObject.getString("icon");
                            //fLog.d("XXX","pic--------------"+product.pic);
                            Log.d(TAG, "product is " + product.id + " " + product.name + " " + productObject
                                    .getString("icon"));
                            mRelatedProducts.add(product);
                        }
                    }
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(json);
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            showProgress();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(ArticleDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                mNotFoundView.setVisibility(View.VISIBLE);
            } else {
                webView.loadDataWithBaseURL(URLConstant.SERVER_ADDRESS, content, "text/html", "UTF-8", null);
                likeView.setText(likeCount);
                titleView.setText(barTitle);
                if (isFavorite) {
                    favButton.setImageResource(R.drawable.button_fav_pressed);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    public class RelatedProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRelatedProducts.size();
        }

        @Override
        public Product getItem(int arg0) {
            return mRelatedProducts.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView: " + position);
            ImageView v = (ImageView) convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = (ImageView) inflater.inflate(R.layout.related_product_list_item, parent, false);
            }
            final Product p = getItem(position);
            ImageLoader.getInstance().displayImage(p.pic, v);
            Log.d("XXX",p.pic+"----------------");
            if (v != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "product " + p.id + " is clicked");
                        Intent intent = new Intent(ArticleDetailActivity.this, ProductDetailActivity.class);
                        intent.putExtra(ProductDetailActivity.PRODUCT_ID, p.id);
                        startActivity(intent);
                    }
                });
            }
            return v;
        }

    }

    private class LikeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = NetUtil.getLikeArticleUrl(context, itemId);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "LikeTask, result is " + result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    errorMsg = null;
                    likeCount = Integer.toString(obj.getInt("info"));
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
            if (errorMsg != null) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            } else {
                likeView.setText(likeCount);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    private class FavTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "FavTask, before enter it, isFavorite is " + isFavorite);
            String url = isFavorite ? NetUtil.getCancelFavArticleUrl(context, itemId) : NetUtil
                    .getFavArticleUrl(context, itemId);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();      //想不通，为什么用doget，应该是往数据库插入一条数据？？
            Log.d(TAG, "FavTask, result is " + result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    errorMsg = null;
                    isFavorite = !isFavorite;
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(obj);
                }
                Log.d(TAG, "FavTask, before leave it, isFavorite is " + isFavorite);

            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }
            if (isFavorite) {
                favButton.setImageResource(R.drawable.button_fav_pressed);
            } else {
                favButton.setImageResource(R.drawable.button_fav_normal);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            showProgress();

        }
    }

    public void onBack(View v) {
        onBackPressed();
    }

    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

       
        public void openUrl(String url) {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.PRODUCT_ID, url.substring(url.lastIndexOf("://") + 3));
            startActivity(intent);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
            //return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            addImageClickListener();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }
}
