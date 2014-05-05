package com.maple.beautyjournal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.i2mobi.widget.ChineseStyleSpan;
import com.maple.beautyjournal.base.BaseActivity;
import com.maple.beautyjournal.entitiy.Comment;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kii.wine.components.gallery.AdapterView.OnItemSelectedListener;
import kii.wine.components.gallery.Gallery;
import uk.co.jasonfry.android.tools.ui.PageControl;

public class ProductDetailActivity extends BaseActivity implements View.OnClickListener {

    // TODO: modify the whole UI of ProductDetailActivity
    public static final String PRODUCT_ID = "product_id";
    private String id;
    private int commentCount;
    private String[] images;
    private Context context;
    private Gallery gallery;
    private PageControl pageControl;
    private ProductImageAdapter adapter;
    private ImageView backButton, favView, addCommentView;
    private TextView priceView, likeView, functionView, commentCountView, stockView, brandView, moreCommentsView,titleView;
    private View introDetail;
    private View introDetailChild;
    private Button addKartButton;
    private Product mProduct;
    private LinearLayout linearLayout;
    
    private View mProductScroolView;
    private View mProductNotFoundView;
    
    List<Comment> comments = new ArrayList<Comment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        // customizeActionBar();
        Intent intent = getIntent();
        if (intent.hasExtra(PRODUCT_ID)) {
            id = intent.getStringExtra(PRODUCT_ID);
            Log.d("XXX",id);
        }
        context = this;
        initViews();
        Log.d("XXX","启动线程");
        new GetContentTask().execute();
    }

    @Override
    public void onBackPressed(){
    	if(introDetail.getVisibility() == View.VISIBLE){
    		unloadIntroDetail();
    	}else{
    		super.onBackPressed();
    	}
    }
    
    private void initViews() {
    	mProductScroolView = findViewById(R.id.product_scrollview);
    	mProductNotFoundView = findViewById(R.id.product_not_found_view);
        gallery = (Gallery) findViewById(R.id.gallery);
        adapter = new ProductImageAdapter();
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(kii.wine.components.gallery.AdapterView<?> parent, View view, int i, long l) {
                pageControl.setCurrentPage(i);

            }

            @Override
            public void onNothingSelected(kii.wine.components.gallery.AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });

        gallery.setAdapter(adapter);
        titleView = (TextView)findViewById(R.id.pro_title);
        pageControl = (PageControl) findViewById(R.id.page_control);
        favView = (ImageView) findViewById(R.id.fav);
        favView.setOnClickListener(this);
        addCommentView = (ImageView) findViewById(R.id.submit_comment_view);
        addCommentView.setOnClickListener(this);
        priceView = (TextView) findViewById(R.id.price);
        likeView = (TextView) findViewById(R.id.like);
        likeView.setOnClickListener(this);
        functionView = (TextView) findViewById(R.id.pro_fun);
        brandView = (TextView) findViewById(R.id.pro_brand);
        stockView = (TextView) findViewById(R.id.pro_store);
        introDetail = findViewById(R.id.instro_detail);
        introDetailChild = findViewById(R.id.instro_detail_child);
        commentCountView = (TextView) findViewById(R.id.comment_count);
        addKartButton = (Button) findViewById(R.id.add_kart);
        addKartButton.setOnClickListener(this);

        backButton = (ImageView) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
            	onBackPressed();
            }

        });
                
        OnClickListener introDetailOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
            	unloadIntroDetail();
            }
        };
        introDetail.setOnClickListener(introDetailOnClickListener);
        introDetailChild.setOnClickListener( introDetailOnClickListener);
        
        View ben_intro = findViewById(R.id.pro_instro);
        ben_intro.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
            	loadIntroDetail();
            }

        });

        linearLayout = (LinearLayout) findViewById(R.id.comments_list);
        moreCommentsView = (TextView) findViewById(R.id.more_comment);
        moreCommentsView.setOnClickListener(this);
        TextView store = (TextView)findViewById(R.id.pro_seller);
        store.setVisibility(View.VISIBLE);
        store.setText(getString(R.string.default_store_name));
    }
    
    private void loadIntroDetail(){
        introDetail.setVisibility(View.VISIBLE);

        introDetail.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom));
        TextView title = (TextView) introDetail.findViewById(R.id.intro_view_title);
        title.setText(mProduct.brand);
        TextView contentView = (TextView) introDetail.findViewById(R.id.intro_view_content);
        contentView.setText(Html.fromHtml(mProduct.description));
    }
    
    private void unloadIntroDetail(){
        introDetail.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_to_bottom));
        introDetail.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fav:
                if (SettingsUtil.isLoggedIn(context)) {
                    new FavProductTask().execute();
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(context, getString(R.string.pls_log_in_first), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.like:
                new LikeProductTask().execute();
                break;
            case R.id.add_kart:
                SettingsUtil.addProductToKart(this, mProduct);
                showToast(getString(R.string.product_has_been_put_into_kart));
                break;
            case R.id.submit_comment_view:
                Intent intent = new Intent(this, SubmitCommentActivity.class);
                intent.putExtra(SubmitCommentActivity.PRODUCT_ID_EXTRA, mProduct.id);
                startActivityForResult(intent, 0);
                break;
            case R.id.more_comment:
                if (commentCount > 3) {
                    Intent moreCommentsIntent = new Intent(ProductDetailActivity.this, MoreCommentActivity.class);
                    moreCommentsIntent.putExtra(MoreCommentActivity.PRODUCT_ID, id);
                    startActivity(moreCommentsIntent);
                } else {
                    Toast.makeText(context, getString(R.string.no_more_comments), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public class GetContentTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = NetUtil.getProductDetailUrl(context, id);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d("XXX", "doInBackground: " + result);
            String ret;
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    JSONObject info = json.getJSONObject("info");
                    mProduct = Product.fromJson(info);
                    images = info.optString("item_image").split(";");
                    for(int i = 0; i < images.length; i++) {
                        images[i] = URLConstant.SERVER_ADDRESS + images[i];
                    }
                    if (images.length > 0) { mProduct.pic = images[0]; }
                    ret = null;
                } else {
                    ret = ServerDataUtils.getErrorMessage(json);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ret = e.getLocalizedMessage();
            }
            return ret;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            showProgress();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                dismissProgress();
                if (!TextUtils.isEmpty(s)) {
                    showToast(s);
                    ShowErrorView();
                } else {
                    new GetCommentsTask().execute();
                    pageControl.setPageCount(images.length);
                    pageControl.setActiveDrawable(getResources().getDrawable(R.drawable.slideshow_selected));
                    pageControl.setCurrentPage(0);
                    priceView.setText("￥" + mProduct.price);
                    likeView.setText(Integer.toString(mProduct.like));
                    functionView.setText(String.format(getString(R.string.function_status), mProduct.functions));
                    brandView.setText(String.format(getString(R.string.brand_status), mProduct.brand));
//                    stockView.setText(String.format(getString(R.string.stock_status), mProduct.stock));
//                    stockView.setVisibility(View.VISIBLE);
                    favView.setImageResource(mProduct.favorite ? R.drawable.button_fav_pressed : R.drawable
                            .button_fav_normal);
                    titleView.setText(mProduct.name);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ShowErrorView();
            }
        }
    }

    private void ShowErrorView(){
    	this.mProductScroolView.setVisibility(View.GONE);
    	this.addKartButton.setVisibility(View.GONE);
    	this.mProductNotFoundView.setVisibility(View.VISIBLE);
    }
    
    private String errorMsg;

    public class LikeProductTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = NetUtil.getLikeProductUrl(context, id);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "LikeTask, result is " + result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    errorMsg = null;
                    mProduct.like = obj.getInt("info");
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                likeView.setText(Integer.toString(mProduct.like));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    public class FavProductTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = mProduct.favorite ? NetUtil.getCancelFavProductUrl(context, id) : NetUtil
                    .getFavProductUrl(context, id);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "FavTask, result is " + result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    errorMsg = null;
                    mProduct.favorite = !mProduct.favorite;
                    
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(obj);
                    Log.d(TAG, "errorMsg is " + errorMsg);
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
            favView.setImageResource(mProduct.favorite ? R.drawable.button_fav_pressed : R.drawable
                    .button_fav_normal);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            showProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }
            if (mProduct.favorite) {
                favView.setImageResource(R.drawable.button_fav_pressed);
            } else {
                favView.setImageResource(R.drawable.button_fav_normal);
            }
        }
    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    private class ProductImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (images != null) {
                return images.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return images[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView v = (ImageView) convertView;
            if (v == null) {
                v = new ImageView(context);

                v.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                v.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                           ViewGroup.LayoutParams.MATCH_PARENT));
                v.setPadding(Utils.dip2px(context, 10), 0, Utils.dip2px(context, 10), 0);
            }
            Log.d(TAG, "image is " + images[position]);
            ImageLoader.getInstance().displayImage(images[position], v);
            return v;
        }
    }

    private static final int DEFAULT_COMMENT_SIZE = 3;
    private int offset = 0;

    private class GetCommentsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String url = NetUtil.getProductCommentsUrl(context, id, DEFAULT_COMMENT_SIZE, offset);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "result is " + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(jsonObject)) {
                    JSONObject info = jsonObject.getJSONObject("info");
                    JSONArray array = info.optJSONArray("items");
                    commentCount = info.optInt("count");
                    if (array == null || array.length() == 0) {
                        return null;
                    }
                    comments.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Comment comment = ServerDataUtils.getCommentFromJSONObject(obj);
                        comments.add(comment);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //fill in LinearLayout
            if (comments.size() == 0) {
                linearLayout.setVisibility(View.GONE);
                return;
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
            linearLayout.removeAllViews();
            for (Comment comment : comments) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.comment_list_item, linearLayout, false);
                ImageView iv = (ImageView) view.findViewById(R.id.image);
                ImageLoader.getInstance().displayImage(comment.image, iv);
                TextView tv1 = (TextView) view.findViewById(R.id.date);
                Date d = new Date(comment.time);
                tv1.setText(DateFormat.format("yy-MM-dd", d));
                TextView tv2 = (TextView) view.findViewById(R.id.content);
                String comments = comment.username + ": " + comment.content;
                SpannableString ss = new SpannableString(comments);
				ss.setSpan(new ChineseStyleSpan(Typeface.BOLD), 0, comment.username.length()+1
						, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv2.setText(ss);                
                RatingBar rating = (RatingBar) view.findViewById(R.id.rate);
                if (comment.star == 0) {
                    comment.star = 3;
                }
                rating.setNumStars(comment.star);
                linearLayout.addView(view);
            }
            commentCountView.setText(String.format(getString(R.string.comment_count), commentCount));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult, " + requestCode + ", resultCode is " + resultCode);
        if (resultCode == RESULT_OK) {
            new GetCommentsTask().execute();
        }
    }
}
