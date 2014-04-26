package com.maple.beautyjournal.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.app.lib.Keys;
import com.alipay.android.app.lib.Result;
import com.alipay.android.app.lib.Rsa;
import com.alipay.android.app.sdk.AliPay;
import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.OrderListActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Address;
import com.maple.beautyjournal.entitiy.Product;
import com.maple.beautyjournal.provider.Beauty;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutFragment extends BaseFragment implements OnClickListener {
    private Context context;
    private Address currentAddress;
    View address, deliver, payMode;
    TextView addressText, deliverText, payModeText;
    private static final String TAG = CheckoutFragment.class.getName();
    private boolean isPayAtArrival = true;
    private LinearLayout pro_list;
    private List<Product> products;
    String totalPrice;
    float total;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_checkout, container, false);
        ImageView back = (ImageView) v.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }

        });
        pro_list = (LinearLayout) v.findViewById(R.id.pro_list);
        context = getActivity();
        address = v.findViewById(R.id.address);
        address.setOnClickListener(this);
        deliver = v.findViewById(R.id.deliever);
        deliver.setOnClickListener(this);
        payMode = v.findViewById(R.id.pay_mode);
        payMode.setOnClickListener(this);
        totalPrice = getArguments().getString("total_price");
        Log.d(TAG, "total price is " + totalPrice);
        fillProList();
        Button pay = (Button) v.findViewById(R.id.submit_order);
        pay.setOnClickListener(this);
        initTextViews(v);
        return v;
    }


    //pos 0 top,1 middle,2 bottom
    private View getProListItem(int pos) {
        LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = lf.inflate(R.layout.pay_pro_list_item, null, false);
        int bg;
        if (products.size()==1) {
            bg = R.drawable.input_bg_bottom;
        } else if (pos == 0) {
            bg = R.drawable.input_bg_top;
        } else if (pos < products.size() - 1) {
            bg = R.drawable.input_bg_middle;
        } else {
            bg = R.drawable.input_bg_bottom;
        }
        v.setBackgroundResource(bg);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView price = (TextView) v.findViewById(R.id.price);
        TextView count = (TextView) v.findViewById(R.id.count);
//        title.setText("Title");
//        price.setText("Price");
//        count.setText("Count");
        Product product = products.get(pos);
        title.setText(product.name);
        price.setText(product.price);
        count.setText(Integer.toString(product.count));
        getActivity().getSharedPreferences(SettingsUtil.PREF_NAME, Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(listener);
        return v;
    }

    private void fillProList() {
        products = SettingsUtil.getProductsInKart(getActivity());
        for (int i = 0; i < products.size(); i++) {
            pro_list.addView(getProListItem(i));
        }
    }

    private List<Address> addresses = new ArrayList<Address>();

    private void initTextViews(View v) {
        addressText = (TextView) v.findViewById(R.id.address_text);
        deliverText = (TextView) v.findViewById(R.id.delivery_text);
        payModeText = (TextView) v.findViewById(R.id.pay_mode_text);
        setDelivery();
        loadAddresses();
        if (addresses.size() == 1) {
            currentAddress = addresses.get(0);
        } else {
            for (int i = 0; i < addresses.size(); i++) {
                Address address = addresses.get(i);
                if (address.isDefault) {
                    currentAddress = address;
                    currentAddressPosition = i;
                    break;
                }
            }
            if (currentAddress == null && addresses.size() > 0) {
                currentAddress = addresses.get(0);
                currentAddressPosition = 0;
            }
        }
        if (currentAddress != null && !TextUtils.isEmpty(currentAddress.address)) {
            setAddress();
            setPayMode();
        } else {
            Toast.makeText(getActivity(), getString(R.string.add_address_first), Toast.LENGTH_SHORT).show();
            Runnable runnable = new Runnable() {
                public void run() {
                    Bundle bundle = new Bundle();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    bundle.putString("opt", "add");
                    Fragment add_address = Fragment
                            .instantiate(getActivity(), Add_View_AddressFragment.class.getName(), bundle);
                    ft.add(R.id.realtabcontent, add_address);
                    ft.commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                }
            };
            payMode.postDelayed(runnable, 1000);
        }
        TextView tv = (TextView) v.findViewById(R.id.pro_total);
        tv.setText(totalPrice);
        float price = 0;
        try {
            Number n1 = NumberFormat.getCurrencyInstance(Locale.getDefault()).parse(totalPrice);
            Number n3 = NumberFormat.getInstance(Locale.getDefault()).parse(totalPrice.substring(1));
            Log.d(TAG, "format price: " + totalPrice + " result is " + n1 + ", " + n3);
            price = Float.parseFloat(n1.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tv = (TextView) v.findViewById(R.id.deliever_price);
        if (price > ConstantsHelper.FREE_SHIPPING_FEE) {
            tv.setText("￥0.00");
        } else {
            tv.setText("￥10.00");
        }
        tv = (TextView) v.findViewById(R.id.total);
        mShoppingFee = (price > ConstantsHelper.FREE_SHIPPING_FEE ) ? 0 : ConstantsHelper.SHIPPING_FEE;
        total = price+mShoppingFee;
        tv.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(total));
    }
    
    private float mShoppingFee = 0;

    private void loadAddresses() {
        String addressListString = SettingsUtil.getAddressList(getActivity());
        if (!TextUtils.isEmpty(addressListString)) {
            try {
                JSONArray addressArray = new JSONArray(addressListString);
                if (addressArray.length() > 0) {
                    addresses.clear();
                    for (int i = 0; i < addressArray.length(); i++) {
                        JSONObject obj = addressArray.getJSONObject(i);
                        Address address = Address.fromJson(obj);
                        addresses.add(address);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAddress() {
        addressText.setText(currentAddress.address + " " + currentAddress.phone + " " + currentAddress.name);
    }

    private void showAddressList() {
        if (addresses.size() == ConstantsHelper.MAX_ADDRESS_COUNT) {
            Toast.makeText(getActivity(), String
                    .format(getString(R.string.max_address_reached), ConstantsHelper.MAX_ADDRESS_COUNT), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Bundle bundle = new Bundle();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        bundle.putString("opt", "add");
        Fragment add_address = Fragment.instantiate(getActivity(), Add_View_AddressFragment.class.getName(), bundle);
        ft.add(R.id.realtabcontent, add_address);
        ft.commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    private void setDelivery(){
        String deliverMode = getResources().getStringArray(R.array.delivery_mode)[currentDeliveryPosition];
        deliverText.setText(deliverMode);    	
    }
    
    private void setPayMode() {
        isPayAtArrival = false;
        if (currentAddress != null) {
            try {
                Log.d(TAG, "setPaymode, province is " + currentAddress.province + ", " +
                        "city: " + currentAddress.city + ", district: " + currentAddress.district);
                Cursor c = context.getContentResolver()
                        .query(Beauty.Area.CONTENT_URI, new String[]{Beauty.Area.PAY_AT_ARRIVAL},
                               Beauty.Area.PROVINCE + "=? AND " + Beauty.Area.CITY + " = ? AND " + Beauty.Area
                                       .DISTRICT +
                                " =" +
                                " ?", new String[]{currentAddress.province, currentAddress.city,
                                currentAddress.district}, null);
                if (c != null && c.moveToFirst()) {
                    isPayAtArrival = (c.getInt(0) == 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
        if (isPayAtArrival) {
            payModeText.setText(String.format(getString(R.string.pay_mode), getString(R.string.pay_on_deliver)));
        } else {
            payModeText.setText(String.format(getString(R.string.pay_mode), getString(R.string.alipay)));
        }
    }

    int currentDeliveryPosition = 0;
    private DialogInterface.OnClickListener deliveryListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            currentDeliveryPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
            setDelivery();
            dialogInterface.dismiss();
        }
    };

    int currentAddressPosition = -1;
    private DialogInterface.OnClickListener addressListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            currentAddressPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
            currentAddress = addresses.get(currentAddressPosition);
            setAddress();
            setPayMode();
            dialogInterface.dismiss();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.address:
                String[] addressItems = new String[addresses.size()];
                for (int i = 0; i < addresses.size(); i++) {
                    addressItems[i] = addresses.get(i).address;
                }
                Log.d(TAG, "before show address dialog, current position is " + currentAddressPosition);
                new AlertDialog.Builder(context)
                        .setSingleChoiceItems(addressItems, currentAddressPosition, addressListener)
                        .setNeutralButton(getString(R.string.add_address), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showAddressList();
                            }
                        }).setNegativeButton(getString(android.R.string.cancel), null).show();
                break;
            case R.id.deliever:
                new AlertDialog.Builder(getActivity()).setSingleChoiceItems(getResources()
                                                                                    .getStringArray(R.array.delivery_mode), currentDeliveryPosition, deliveryListener)
                        .setNegativeButton(getString(android.R.string.cancel), null).show();
                break;
            case R.id.pay_mode:
                break;
            case R.id.submit_order:
                if (!isPayAtArrival) {
//                    final String orderInfo = getOrderInfo();
//                    new Thread() {
//                        public void run() {
//                            String result = new AliPay(getActivity(), mHandler).pay(orderInfo);
//                            Log.i(TAG, "result = " + result);
//                            Message msg = new Message();
//                            msg.what = RQF_PAY;
//                            msg.obj = result;
//                            mHandler.sendMessage(msg);
//                        }
//                    }.start();
                    new CreateOrderTask(true).execute();
                } else {
                    submitOrder();
                }

                break;
        }
    }

    private String getOrderInfo(){
        String info = getNewOrderInfo(getString(R.string.new_order), "body", Float.toString(total));
        Log.d(TAG, "info is " + info);
        String sign = Rsa.sign(info, Keys.PRIVATE);
        Log.d(TAG, "sign is " + sign);
        sign = URLEncoder.encode(sign);
        info += "&sign=\"" + sign + "\"&" + getSignType();
        Log.d(TAG, "start pay");
        Log.d(TAG, "info = " + info);
        return info;
    }
    
    private void submitOrder() {
        if (currentAddress == null || TextUtils.isEmpty(currentAddress.address)) {
            Toast.makeText(getActivity(), R.string.add_address_first, Toast.LENGTH_SHORT).show();
            return;
        }
        new CreateOrderTask(false).execute();
    }
    
    private void goShoppingCart() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle b = new Bundle();
        Fragment checkout = Fragment.instantiate(getActivity(), ShoppingCarFragment.class.getName(), b);
        ft.replace(R.id.realtabcontent, checkout);
        ft.addToBackStack(null);
        ft.commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
        
        Intent intent = new Intent(context, OrderListActivity.class);
        getActivity().startActivity(intent);
    }

    private class CreateOrderTask extends AsyncTask<Void, Void, Void> {
    	private boolean mUseAlipay = false;
    	private boolean mIsOrderSuccess = false;
        private String errorMsg = null;

        private static final int RQF_PAY = 1;
        Handler mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Result payResult = new Result( (String) msg.obj);
                payResult.parseResult();
                Log.d(TAG, "impossiable: " + payResult.getResult());
                switch (msg.what) {
                    case RQF_PAY: {
                    	if(payResult.IsSuccessOrOnProcess()){
                            Toast.makeText(getActivity(), R.string.alipay_success, Toast.LENGTH_SHORT).show();
                            goShoppingCart();
                    	}else{
                            Toast.makeText(getActivity(), R.string.alipay_failed, Toast.LENGTH_SHORT).show();                		
                    	}
                    	if(mIsOrderSuccess){
                    		goShoppingCart();
                    	}
                    }
                    break;
                    default:
                        break;
                }
            };
        };
    	
    	CreateOrderTask(boolean useAlipay ){
    		mUseAlipay = useAlipay;
    		mIsOrderSuccess = false;
    	}
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
            if(mIsOrderSuccess){
                if (!TextUtils.isEmpty(errorMsg)) {
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.order_success), Toast.LENGTH_SHORT).show();
                }
                goShoppingCart();
            }else{
                if (!TextUtils.isEmpty(errorMsg)) {
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }else{
                	//NOTE(shuyinghuang)
                	Log.e(TAG, "is order failed impossible");
                }
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Context context = getActivity();
                NetUtil util = new HttpClientImplUtil(context, NetUtil.getCreateOrderUrl());
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", SettingsUtil.getUserId(context));
                map.put("username", SettingsUtil.getUserName(context));
                map.put("item_id", buildItemId());
                map.put("buyer_default", "0");
                map.put("buyer_name", currentAddress.name);
                map.put("buyer_phone", currentAddress.phone);
                map.put("buyer_address", currentAddress.address);
                String email = SettingsUtil.getEmail(context);
                map.put("buyer_email", email);
                map.put("buyer_zipcode", currentAddress.zip);
                map.put("postage", String.valueOf(mShoppingFee));
                util.setMap(map);
                String result = util.doPost();
                Log.d(TAG, "result is " + result);
                JSONObject object = new JSONObject(result);
                mIsOrderSuccess = ServerDataUtils.isTaskSuccess(object);
                if (mIsOrderSuccess) {
                	//NOTE(shuyinghuang) save failed is not processed yet
                    SettingsUtil.saveProductList(getActivity(), new ArrayList<Product>());
                	if(mUseAlipay){
                    	String orderInfo =CheckoutFragment.this.getOrderInfo();
                        String aliPayResult = new AliPay(getActivity(), mHandler).pay(orderInfo);
                        Result payResult = new Result( aliPayResult );
                        payResult.parseResult();
                        Log.d(TAG, payResult.getResult());
                	    if(payResult.IsSuccessOrOnProcess()){
                            errorMsg = null;
                    	}else{
                    		errorMsg = getResources().getString(R.string.alipay_failed);
                    	}
                	}
                } else {
                    errorMsg = object.getString("info");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getLocalizedMessage();
            }
            return null;
        }

        private String buildItemId() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Product product : products) {
                if (!first) {
                    sb.append(".");
                }
                for(int i=0; i<product.count; i++){
                    sb.append(product.id);
                    if(i < product.count-1){
                    	sb.append(".");
                    }
                }
                first = false;
            }
            return sb.toString();
        }
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private String getNewOrderInfo(String subject, String body, String price) {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Keys.DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append(getOutTradeNo());
        sb.append("\"&subject=\"");
        sb.append(subject);
        sb.append("\"&body=\"");
        sb.append(body);
        sb.append("\"&total_fee=\"");
        sb.append(price);
        sb.append("\"&notify_url=\"");

        // 网址需要做URL编码
        sb.append(URLEncoder.encode("https://www.beautyjournal.com/notify"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Keys.DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"1m");
        sb.append("\"");

        return new String(sb);
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
        Date date = new Date();
        String key = format.format(date);

        java.util.Random r = new java.util.Random();
        key += r.nextInt();
        key = key.substring(0, 15);
        Log.d(TAG, "outTradeNo: " + key);
        return key;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences
            .OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (getActivity() == null) {
                return;
            }
            if (key != null && key.contentEquals(SettingsUtil.ADDRESS_LIST)) {
                loadAddresses();
                if (addresses != null && addresses.size() > 0) {
                    currentAddress = addresses.get(addresses.size() - 1);
                    setAddress();
                    setPayMode();
                }
            }
        }
    };
}
