package com.maple.beautyjournal.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.basemaple.widget.NoScrollListView;
import com.i2mobi.net.URLConstant;
import com.maple.beautyjournal.LoginActivity;
import com.maple.beautyjournal.MainActivity;
import com.maple.beautyjournal.ProductDetailActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShoppingCarFragment extends BaseFragment {
    List<Product> products = new ArrayList<Product>();
    NoScrollListView list;
    ShoppingCarAdapter adapter;
    Activity context;
    View no_item;
    View content_parent;
    private float mTotalPrice;
    private TextView mCartAction = null;
    private TextView mTvTotalPrice = null;
    private TextView mTvTotalCount = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_shopping_car, container, false);
        context = getActivity();
        content_parent = v.findViewById(R.id.content_parent);
        no_item = v.findViewById(R.id.no_item);
        Button quick_go = (Button) v.findViewById(R.id.quick_go);
        quick_go.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                gotoProductPage();
            }

        });
        initList(v);
        initProducts();
        mTvTotalPrice = (TextView) v.findViewById(R.id.total_price);
        mTvTotalCount = (TextView) v.findViewById(R.id.total_count);
        setPriceAndCount();
        mCartAction = (TextView) v.findViewById(R.id.cart_action);
        mCartAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        String manage = getResources().getString(R.string.management);
		        String done = getResources().getString(R.string.done);				
				if(manage.equals(mCartAction.getText())){
					mCartAction.setText(done);
					adapter.setIsEditMode(true);
				}else{
					mCartAction.setText(manage);					
					adapter.setIsEditMode(false);
				}
				adapter.notifyDataSetChanged();
			}
		});

        ImageButton ib = (ImageButton) v.findViewById(R.id.pay);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SettingsUtil.isLoggedIn(getActivity())) {
                    if (mTotalPrice < ConstantsHelper.FREE_SHIPPING_FEE) {
                        new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.free_shipping_not_reached))
                                .setPositiveButton(getString(R.string.continue_shopping), new DialogInterface.OnClickListener() {


                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        gotoProductPage();
                                    }
                                })
                                .setNegativeButton(getString(R.string.go_checkout), new DialogInterface
                                        .OnClickListener() {


                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        goCheckOut();
                                    }
                                }).show();
                    } else {
                        goCheckOut();
                    }

                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(intent, 0);
                }
            }
        });
        return v;
    }

    private void gotoProductPage() {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.setTab(0);
        }
        //NOTE(shuyinghuang) something is wrong here
        if(getActivity() == null){
        	return;
        }
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment product_cate = Fragment
                .instantiate(getActivity(), ProductCategoryFragment.class.getName(), null);

        ft.replace(R.id.realtabcontent, product_cate);
        ft.addToBackStack(null);
        ft.commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    private void goCheckOut() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
		SettingsUtil.saveProductList(context, products);
        FragmentTransaction ft = fm.beginTransaction();
        String totalPrice = getTotalPrice();
        Bundle b = new Bundle();
        b.putString("total_price", totalPrice);
        Fragment checkout = Fragment.instantiate(getActivity(), CheckoutFragment.class.getName(), b);
        ft.replace(R.id.realtabcontent, checkout);
        ft.addToBackStack(null);
        ft.commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            goCheckOut();
        }
    }

    private void initProducts() {
        if (ConstantsHelper.TEST_SHOPPING_KART) {
            loadMockData();
        } else {
            products = SettingsUtil.getProductsInKart(context);
        }
        Log.d(TAG, "initProducts, products size is " + products.size());
        if (products.size() == 0) {
            no_item.setVisibility(View.VISIBLE);
            content_parent.setVisibility(View.GONE);
        }
    }

    private void setPriceAndCount() {
        if (products.size() == 0) {
            mTvTotalPrice.setText("￥0");
        } else {
            mTvTotalPrice.setText(getTotalPrice());
        }
        if (products.size() == 0) {
        	mTvTotalCount.setText("0");
        } else {
        	mTvTotalCount.setText(""+getTotalCount());
        }
    }

    private void loadMockData() {
        for (int i = 0; i < 5; i++) {
            Product p = new Product();
            p.pic = "http://42.96.185.57:8008/images/mainpic/187_1_720_240.jpg";
            p.name = "TEST PRODUCT " + i;
            p.id = "100" + i;
            p.price = "98";
            products.add(p);
        }
        adapter.notifyDataSetChanged();
    }

    private String getTotalPrice() {
        mTotalPrice = 0;
        for (Product p : products) {
            mTotalPrice += Double.parseDouble(p.price) * p.count;
        }
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(mTotalPrice);
    }
    
    private int getTotalCount(){
        int totalCount = 0;
        for (Product p : products) {
        	totalCount += p.count;
        }
        return totalCount;
    }

    private void initList(View v) {
        list = (NoScrollListView) v.findViewById(R.id.list);
        adapter = new ShoppingCarAdapter();
        list.setAdapter(adapter);
    }


    public class ShoppingCarAdapter extends BaseAdapter {

    	private boolean mIsEditMode = false;

		public void setIsEditMode(boolean isEditMode) {
			this.mIsEditMode = isEditMode;
		}

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Product getItem(int arg0) {
            return products.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = context.getLayoutInflater().inflate(R.layout.shopping_kart_list_item_new, parent, false);
            }

            ImageView iv = (ImageView) v.findViewById(R.id.image);
            final Product product = getItem(position);
            Log.d(TAG, "getView, product is " + product.star + ", " + product.name);
            //iv.setImageBitmap(loader.loadImage(product.imageUrl, adapter, context));
            //iv.setImageDrawable(getAdDrawable(position));
            
            iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(product != null){
						Intent intent = new Intent(getActivity(),ProductDetailActivity.class);
						intent.putExtra(ProductDetailActivity.PRODUCT_ID, product.id);
						getActivity().startActivity(intent);				
					}					
				}
			});
            
            if (!TextUtils.isEmpty(product.pic)) {
                if (product.pic.startsWith("http")) {
                    ImageLoader.getInstance().displayImage(product.pic, iv);
                } else {
                    ImageLoader.getInstance().displayImage(URLConstant.SERVER_ADDRESS + product.pic, iv);
                }
            }else{
            	iv.setImageResource(R.drawable.default_product);
            }
            TextView tv1 = (TextView) v.findViewById(R.id.line1Text);
            tv1.setText(product.name);
            TextView price = (TextView) v.findViewById(R.id.price);
            price.setText("￥" + product.price);
            final TextView count = (TextView) v.findViewById(R.id.count);
            count.setText(""+product.count);
            ImageView delete = (ImageView) v.findViewById(R.id.delete_icon);
            if(mIsEditMode){
            	delete.setVisibility(View.VISIBLE);
            	delete.setFocusable(false);
            	final int pos = position;
            	delete.setOnClickListener(new View.OnClickListener() {
            		@Override
            		public void onClick(View view) {
            			AlertDialog.Builder builder = new AlertDialog.Builder(context);
            			builder.setTitle(getString(R.string.delete_from_kart))
            			.setMessage(String.format(getString(R.string.confirm_delete_from_kart), product.name))
            			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            				@Override
            				public void onClick(DialogInterface dialog, int which) {
            					products.remove(pos);
            					SettingsUtil.saveProductList(context, products);
            					handler.sendEmptyMessage(MSG_REFRESH_LIST);  	
            			    }
            		    }).setNegativeButton(android.R.string.cancel, null).show();
                    }
                });
            }else{
            	delete.setVisibility(View.GONE);
            }
            ImageView buyMinus = (ImageView) v.findViewById(R.id.buy_minus);
            buyMinus.setFocusable(false);
            buyMinus.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(product.count>1){
						product.count--;
			            count.setText(""+product.count);
			            ShoppingCarFragment.this.setPriceAndCount();
					}
				}
			});            
            ImageView buyPlus = (ImageView) v.findViewById(R.id.buy_plus);            
            buyPlus.setFocusable(false);
            buyPlus.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    product.count++;
			        count.setText(""+product.count);
		            ShoppingCarFragment.this.setPriceAndCount();
				}
			});
            return v;
        }

    }

    private Drawable getAdDrawable(int position) {
        switch (position % 5) {
            case 0:
                return getResources().getDrawable(R.drawable.ad1);
            case 1:
                return getResources().getDrawable(R.drawable.ad2);
            case 2:
                return getResources().getDrawable(R.drawable.ad3);
            case 3:
                return getResources().getDrawable(R.drawable.ad4);
            case 4:
                return getResources().getDrawable(R.drawable.ad5);
        }
        return null;
    }

    private static final int MSG_REFRESH_LIST = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LIST:
                    adapter.notifyDataSetChanged();
                    setPriceAndCount();
                    break;
            }
        }
    };
}