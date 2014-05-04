package com.maple.beautyjournal.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.ProductDetailActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//<<<<<<< HEAD
/*
public class ProductListFragment extends BaseFragment implements OnClickListener {
    private static final int TAB_HOT = 0;
    private static final int TAB_PRICE = 1;
    private static final int TAB_SELL = 2;
    private int currentTab;

    private FrameLayout tab_hot;
    private FrameLayout tab_price;
    private FrameLayout tab_sell;

    private String category;
    private String function;
    private String brand;
    private String listBy;
    private String errorMsg;
    private int offset, lastOffset = 0;
    private String orderBy = ORDER_BY_LIKE;
    private List<Product> hotProducts = new ArrayList<Product>();
    private List<Product> priceProducts = new ArrayList<Product>();
    private List<Product> salesProducts = new ArrayList<Product>();

    private static final String ORDER_BY_LIKE = "like";
    private static final String ORDER_BY_PRICE = "price";
    private static final String ORDER_BY_SALES = "sales";
    private static final int DEFAULT_PAGE_SIZE = 5;

    public static final String PRODUCT_CATEGORY = "com.maple.beautyjournal.product.category";
    public static final String LIST_BY_CATEGORY = "com.maple.beautyjournal.list_by_category";
    public static final String LIST_BY_FUNCTION = "com.maple.beautyjournal.list_by_function";
    public static final String LIST_BY_BRAND = "com.maple.beautyjournal.list_by_brand";

    private static final Map<Integer, String> sOrderByMap = new HashMap<Integer, String>();

    static {
        sOrderByMap.put(TAB_HOT, ORDER_BY_LIKE);
        sOrderByMap.put(TAB_PRICE, ORDER_BY_PRICE);
        sOrderByMap.put(TAB_SELL, ORDER_BY_SALES);
    }

    private PullToRefreshListView list;
    private ProductListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_product_list, container, false);
        ImageView back = (ImageView) v.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ProductListFragment.this.getActivity().onBackPressed();
            }

        });
         initTab(v);
         initList(v);
         initQueryParameter();
         setTab(TAB_HOT);
        return v;
    }

    private void initQueryParameter() {
        Bundle bundle = getArguments();
        if (bundle.containsKey(LIST_BY_BRAND)) {
            brand = bundle.getString(LIST_BY_BRAND);
            listBy = "listbybrand/brand/" + brand;
        } else if (bundle.containsKey(LIST_BY_CATEGORY)) {
            category = bundle.getString(LIST_BY_CATEGORY);
            listBy = "listbycat/cat/" + category;
        } else if (bundle.containsKey(LIST_BY_FUNCTION)) {
            function = bundle.getString(LIST_BY_FUNCTION);
            listBy = "listbyfunction/function/" + function;
        } else {
            //default, for test
            category = "3302";
            listBy = "listbycat/cat/" + category;
        }
        orderBy = sOrderByMap.get(currentTab);
    }

    private void initList(View v) {
        list = (PullToRefreshListView) v.findViewById(R.id.list);
        list.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Product> products = findProducts();
                Product product = products.get(i - 1);
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.PRODUCT_ID, product.id);
                startActivity(intent);
                Log.d(TAG, "product is " + product.id + ", " + product.name + ", " + product.brand);
            }
        });
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new GetDataTask().execute();
            }
        });
        adapter = new ProductListAdapter();
        list.setAdapter(adapter);
    }

    private void initTab(View parent) {
        tab_hot = (FrameLayout) parent.findViewById(R.id.tab_hot);
        tab_hot.setOnClickListener(this);
        tab_price = (FrameLayout) parent.findViewById(R.id.tab_price);
        tab_price.setOnClickListener(this);
        tab_sell = (FrameLayout) parent.findViewById(R.id.tab_sell);
        tab_sell.setOnClickListener(this);
    }

    private void setTab(int tab) {
        currentTab = tab;
        tab_hot.getChildAt(1).setVisibility(View.GONE);
        tab_price.getChildAt(1).setVisibility(View.GONE);
        tab_sell.getChildAt(1).setVisibility(View.GONE);

        switch (currentTab) {
            case TAB_HOT:
                tab_hot.getChildAt(1).setVisibility(View.VISIBLE);

                break;
            case TAB_PRICE:
                tab_price.getChildAt(1).setVisibility(View.VISIBLE);

                break;
            case TAB_SELL:
                tab_sell.getChildAt(1).setVisibility(View.VISIBLE);
                break;
        }
        orderBy = sOrderByMap.get(currentTab);
        //need to retrieve data from server;
        new GetDataTask().execute();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == tab_hot) {
            setTab(TAB_HOT);
        } else if (arg0 == tab_price) {
            setTab(TAB_PRICE);
        } else if (arg0 == tab_sell) {
            setTab(TAB_SELL);
        }

    }


    public static final Bundle productParameterBundle(String category) {
        Bundle b = new Bundle();
        b.putString(PRODUCT_CATEGORY, category);
        return b;
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Context context = getActivity();
            String url = NetUtil.getProductListUrl2(context, listBy, orderBy, DEFAULT_PAGE_SIZE, offset);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "result is " + result);
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    JSONObject info = json.getJSONObject("info");
                    JSONArray items = info.optJSONArray("items");
                    if (items == null || items.length() == 0) {
                        errorMsg = getString(R.string.error_no_more_items);
                        return null;
                    }
                    lastOffset = offset;
                    List<Product> products = findProducts();
                    for (int i = 0; i < items.length(); i++) {
                        Product product = ServerDataUtils.getProductFromJSONObject(items.getJSONObject(i));
                        products.add(product);
                    }
                    offset += items.length();
                    adapter.setItems(products);
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(json);
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            adapter.notifyDataSetChanged();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                errorMsg = null;
            }
            list.onRefreshComplete();
            if (offset == lastOffset) {
                list.setMode(PullToRefreshBase.Mode.DISABLED);
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            if (findProducts().size() == 0) {
                showProgress();
            }
        }
    }

    private List<Product> findProducts() {
        switch (currentTab) {
            case TAB_HOT:
                return hotProducts;
            case TAB_PRICE:
                return priceProducts;
            case TAB_SELL:
                return salesProducts;
        }
        return hotProducts;
    }


    private class ProductListAdapter extends BaseAdapter {
        private List<Product> products;

        public void setItems(List<Product> products) {
            this.products = products;
        }

        @Override
        public int getCount() {
            if (products != null) {
                return products.size();
            }
            return 0;
        }

        @Override
        public Product getItem(int i) {
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
                v = LayoutInflater.from(getActivity()).inflate(R.layout.product_list_item, parent, false);
            }
            Product product = getItem(position);
            Log.d(TAG, "getView, product is " + product.name + ", star is " + product.star);
            TextView tv1 = (TextView) v.findViewById(R.id.line1Text);
            tv1.setText(product.name);
            TextView price = (TextView) v.findViewById(R.id.price);
            price.setText("￥" + product.price);
            TextView tv2 = (TextView) v.findViewById(R.id.line2Text);
            tv2.setText(Integer.toString(product.comment));
            RatingBar rating = (RatingBar) v.findViewById(R.id.rate);
            if (product.star == 0) { product.star = 3; }
            rating.setNumStars(product.star);
            ImageView iv = (ImageView) v.findViewById(R.id.image);
            if (!TextUtils.isEmpty(product.pic)) {
                if (product.pic.startsWith("http")) {
                    ImageLoader.getInstance().displayImage(product.pic, iv);
                    Log.d("XXX",product.pic+"000000");

                } else {
                    ImageLoader.getInstance().displayImage(URLConstant.SERVER_ADDRESS + product.pic, iv);
                    Log.d("XXX",product.pic+"55555");
                }
            } else {
                iv.setImageResource(R.drawable.default_product);
            }
            View delete = v.findViewById(R.id.delete);
            delete.setVisibility(View.GONE);
            return v;
        }
    }
=======
*/
public class ProductListFragment extends BaseFragment implements
		OnClickListener {
    private static final int TAB_HOT = 0;
    private static final int TAB_PRICE = 1;
    private static final int TAB_SELL = 2;
    private int currentTab;

    private LinearLayout tab_hot;
    private LinearLayout tab_price;
    private LinearLayout tab_sell;

    private String category = "";
    private String function = "";
    private String brand = "";
    private String listBy;
    private String errorMsg;
    private int offset, lastOffset = 0;
    // 每种类型对应一个offset和lastOffset
    private int likeOffset, likeLastOffset = 0;
    private int priceOffset, priceLastOffset = 0;
    private int salesOffset, salesLastOffset = 0;
    private String orderBy = ORDER_BY_LIKE;
    private List<Product> hotProducts = new ArrayList<Product>();
    private List<Product> priceProducts = new ArrayList<Product>();
    private List<Product> salesProducts = new ArrayList<Product>();

    private static final String ORDER_BY_LIKE = "like";
    private static final String ORDER_BY_PRICE = "price";
    private static final String ORDER_BY_SALES = "sales";
    private static final int DEFAULT_PAGE_SIZE = 5;

    public static final String PRODUCT_CATEGORY = "com.maple.beautyjournal.product.category";
    public static final String LIST_BY_CATEGORY = "com.maple.beautyjournal.list_by_category";
    public static final String LIST_BY_FUNCTION = "com.maple.beautyjournal.list_by_function";
    public static final String LIST_BY_BRAND = "com.maple.beautyjournal.list_by_brand";

    private static final Map<Integer, String> sOrderByMap = new HashMap<Integer, String>();

    static {
        sOrderByMap.put(TAB_HOT, ORDER_BY_LIKE);
        sOrderByMap.put(TAB_PRICE, ORDER_BY_PRICE);
        sOrderByMap.put(TAB_SELL, ORDER_BY_SALES);
    }

    private PullToRefreshListView list;
    private ProductListAdapter adapter;
    FragmentActivity context;
    private TextView title;
    private ImageView search;
    private ImageView titleBack;
    private ImageView shopingcar;
    private TextView titleFilter;
    // 由上一级传过来的id和名称
    private String passType = "";
    private String passName = "";

    // 筛选结果赋值
    public static String filterID = "";
    public static String filterType = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterID = "";
        filterType = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        title = (TextView) context.findViewById(R.id.title);
        search = (ImageView) context.findViewById(R.id.search);
        titleBack = (ImageView) context.findViewById(R.id.title_back);
        shopingcar = (ImageView) context.findViewById(R.id.shopingcar);
        titleFilter = (TextView) context.findViewById(R.id.title_filter);
        View v = inflater.inflate(R.layout.activity_product_list, container,
                false);
         ImageView back = (ImageView) v.findViewById(R.id.btn_back);

        titleBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 复原标题
               recoverTitleBar();
               ProductListFragment.this.getActivity().onBackPressed();
            }

        });

        initTab(v);
        initList(v);
        initQueryParameter();
        setTab(TAB_HOT);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListBy();
    }

    /**
     * 设置接口路径
     */
    private void setListBy() {
        boolean mark = false;
        if (filterType.equals(LIST_BY_CATEGORY)) {
            // 有变化再重载
            if (filterID.equals(category) == false) {
                category = filterID;
                mark = true;
            }
        } else if (filterType.equals(LIST_BY_FUNCTION)) {
            // 有变化再重载
            if (filterID.equals(function) == false) {
                function = filterID;
                mark = true;
            }
        } else if (filterType.equals(LIST_BY_BRAND)) {
            // 有变化再重载
            if (filterID.equals(brand) == false) {
                brand = filterID;
                mark = true;
            }
        }
        listBy = "list/brand/" + brand + "/cat/" + category + "/function/"
                + function;
        if (mark) {
            // 数据重置
            hotProducts.clear();
            priceProducts.clear();
            salesProducts.clear();
            offset = 0;
            lastOffset = 0;
            likeOffset = 0;
            likeLastOffset = 0;
            priceOffset = 0;
            priceLastOffset = 0;
            salesOffset = 0;
            salesLastOffset = 0;
            new GetDataTask().execute();
        }
    }

    /**
     * 复原标题栏
     */
    private void recoverTitleBar() {
        title.setText("美人记");
        titleBack.setVisibility(View.GONE);
        search.setVisibility(View.VISIBLE);
        titleFilter.setVisibility(View.GONE);
        shopingcar.setVisibility(View.VISIBLE);
    }

    /**
     * 设置标题栏
     *
     * @param name
     */
    private void setTitleBar(String name) {
        title.setText(name);
        search.setVisibility(View.GONE);
        titleBack.setVisibility(View.VISIBLE);
        shopingcar.setVisibility(View.GONE);
        titleFilter.setVisibility(View.VISIBLE);
    }

    private void initQueryParameter() {
        Bundle bundle = getArguments();
        passName = bundle.getString("name");
        setTitleBar(passName);
        if (bundle.containsKey(LIST_BY_BRAND)) {
            brand = bundle.getString(LIST_BY_BRAND);
            passType = LIST_BY_BRAND;
        } else if (bundle.containsKey(LIST_BY_CATEGORY)) {
            category = bundle.getString(LIST_BY_CATEGORY);
            passType = LIST_BY_CATEGORY;
        } else if (bundle.containsKey(LIST_BY_FUNCTION)) {
            function = bundle.getString(LIST_BY_FUNCTION);
            passType = LIST_BY_FUNCTION;
        } else {
            // default, for test
            category = "3302";
            passType = LIST_BY_CATEGORY;
        }
        setListBy();
        // 设置筛选事件
        titleFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = ProductListFragment.this.getActivity()
                        .getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment productFilter = Fragment.instantiate(
                        ProductListFragment.this.getActivity(),
                        ProductFilterFragment.class.getName(), null);
                Bundle bundle = new Bundle();
                if (passType.equals(LIST_BY_BRAND)) {
                    bundle.putString(ProductListFragment.LIST_BY_BRAND, brand);
                } else if (passType.equals(LIST_BY_CATEGORY)) {
                    bundle.putString(ProductListFragment.LIST_BY_CATEGORY,
                            category);
                } else {
                    bundle.putString(ProductListFragment.LIST_BY_CATEGORY,
                            category);
                }
                bundle.putString("name", passName);
                productFilter.setArguments(bundle);
                ft.replace(R.id.content, productFilter);
                ft.addToBackStack(null);
                ft.commit();
                context.getSupportFragmentManager()
                        .executePendingTransactions();
            }
        });
        orderBy = sOrderByMap.get(currentTab);
    }

    private void initList(View v) {
        list = (PullToRefreshListView) v.findViewById(R.id.list);
        list.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                List<Product> products = findProducts();
                Product product = products.get(i - 1);
                Intent intent = new Intent(getActivity(),
                        ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.PRODUCT_ID, product.id);
                startActivity(intent);
                Log.d(TAG, "product is " + product.id + ", " + product.name
                        + ", " + product.brand);
            }
        });
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                setListBy();
                new GetDataTask().execute();
            }
        });
        adapter = new ProductListAdapter();
        list.setAdapter(adapter);
    }

    private void initTab(View parent) {
        tab_hot = (LinearLayout) parent.findViewById(R.id.tab_hot);
        tab_hot.setOnClickListener(this);
        tab_price = (LinearLayout) parent.findViewById(R.id.tab_price);
        tab_price.setOnClickListener(this);
        tab_sell = (LinearLayout) parent.findViewById(R.id.tab_sell);
        tab_sell.setOnClickListener(this);
    }

    private void setTab(int tab) {
        currentTab = tab;

        switch (currentTab) {
            case TAB_HOT:
                tab_hot.getChildAt(1).setBackgroundResource(R.drawable.order_yes_selected);
                tab_price.getChildAt(1).setBackgroundResource(R.drawable.order_no_selected);
                tab_sell.getChildAt(1).setBackgroundResource(R.drawable.order_no_selected);
                break;
            case TAB_PRICE:
                tab_hot.getChildAt(1).setBackgroundResource(R.drawable.order_no_selected);
                tab_price.getChildAt(1).setBackgroundResource(R.drawable.order_yes_selected);
                tab_sell.getChildAt(1).setBackgroundResource(R.drawable.order_no_selected);
                break;
            case TAB_SELL:
                tab_hot.getChildAt(1).setBackgroundResource(R.drawable.order_no_selected);
                tab_price.getChildAt(1).setBackgroundResource(R.drawable.order_no_selected);
                tab_sell.getChildAt(1).setBackgroundResource(R.drawable.order_yes_selected);
                break;
        }
        // 先将之前的 offset，lastoffset参数保存到对应的类型
        if (orderBy.equals(ORDER_BY_LIKE)) {
            likeOffset = offset;
            likeLastOffset = lastOffset;
        } else if (orderBy.equals(ORDER_BY_PRICE)) {
            priceOffset = offset;
            priceLastOffset = lastOffset;
        } else if (orderBy.equals(ORDER_BY_SALES)) {
            salesOffset = offset;
            salesLastOffset = lastOffset;
        }
        orderBy = sOrderByMap.get(currentTab);
        // 获取新的类型之后重新加载回去
        if (orderBy.equals(ORDER_BY_LIKE)) {
            offset = likeOffset;
            lastOffset = likeLastOffset;
        } else if (orderBy.equals(ORDER_BY_PRICE)) {
            offset = priceOffset;
            lastOffset = priceLastOffset;
        } else if (orderBy.equals(ORDER_BY_SALES)) {
            offset = salesOffset;
            lastOffset = salesLastOffset;
        }
        // need to retrieve data from server;
        setListBy();
        new GetDataTask().execute();
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == tab_hot) {
            setTab(TAB_HOT);
        } else if (arg0 == tab_price) {
            setTab(TAB_PRICE);
        } else if (arg0 == tab_sell) {
            setTab(TAB_SELL);
        }

    }

    public static final Bundle productParameterBundle(String category) {
        Bundle b = new Bundle();
        b.putString(PRODUCT_CATEGORY, category);
        return b;
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Context context = getActivity();
            String url = NetUtil.getProductListUrl2(context, listBy, orderBy,
                    DEFAULT_PAGE_SIZE, offset);
            Log.d(TAG, "doInBackground, url is " + url);
            NetUtil util = new HttpClientImplUtil(context, url);
            String result = util.doGet();
            Log.d(TAG, "result is " + result);
            try {
                JSONObject json = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(json)) {
                    JSONObject info = json.getJSONObject("info");
                    JSONArray items = info.optJSONArray("items");
                    if (items == null || items.length() == 0) {
                        errorMsg = getString(R.string.error_no_more_items);
                        List<Product> products = findProducts();
                        adapter.setItems(products);
                        return null;
                    }
                    lastOffset = offset;
                    List<Product> products = findProducts();
                    for (int i = 0; i < items.length(); i++) {
                        Product product = ServerDataUtils
                                .getProductFromJSONObject(items
                                        .getJSONObject(i));
                        products.add(product);
                    }
                    offset += items.length();
                    adapter.setItems(products);
                } else {
                    errorMsg = ServerDataUtils.getErrorMessage(json);
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            adapter.notifyDataSetChanged();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG)
                        .show();
                errorMsg = null;
            }
            list.onRefreshComplete();
            if (offset == lastOffset) {
                list.setMode(PullToRefreshBase.Mode.DISABLED);
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismissProgress();
            if (findProducts().size() == 0) {
                showProgress();
            }
        }
    }

    private List<Product> findProducts() {
        switch (currentTab) {
            case TAB_HOT:
                return hotProducts;
            case TAB_PRICE:
                return priceProducts;
            case TAB_SELL:
                return salesProducts;
        }
        return hotProducts;
    }

    private class ProductListAdapter extends BaseAdapter {
        private List<Product> products;

        public void setItems(List<Product> products) {
            this.products = products;
        }

        @Override
        public int getCount() {
            if (products != null) {
                return products.size();
            }
            return 0;
        }

        @Override
        public Product getItem(int i) {
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
                v = LayoutInflater.from(getActivity()).inflate(
                        R.layout.product_list_item, parent, false);
            }
            Product product = getItem(position);
            Log.d(TAG, "getView, product is " + product.name + ", star is "
                    + product.star);
            TextView tv1 = (TextView) v.findViewById(R.id.line1Text);
            tv1.setText(product.name);
            TextView price = (TextView) v.findViewById(R.id.price);
            price.setText("￥" + product.price);
            TextView tv2 = (TextView) v.findViewById(R.id.line2Text);
            tv2.setText(Integer.toString(product.comment) + "条评论");
            RatingBar rating = (RatingBar) v.findViewById(R.id.rate);
            if (product.star == 0) {
                product.star = 3;
            }
            rating.setNumStars(product.star);
            ImageView iv = (ImageView) v.findViewById(R.id.image);
            if (!TextUtils.isEmpty(product.pic)) {
                if (product.pic.startsWith("http")) {
                    ImageLoader.getInstance().displayImage(product.pic, iv);
                } else {
                    ImageLoader.getInstance().displayImage(
                            URLConstant.SERVER_ADDRESS + product.pic, iv);
                }
            } else {
                iv.setImageResource(R.drawable.default_product);
            }
            View delete = v.findViewById(R.id.delete);
            delete.setVisibility(View.GONE);
            return v;
        }
    }

//>>>>>>> e540a9903353db32a7e081247f8b2f0cac3064f9
}
