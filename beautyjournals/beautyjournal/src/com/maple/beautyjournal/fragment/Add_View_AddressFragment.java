package com.maple.beautyjournal.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.i2mobi.net.HttpClientImplUtil;
import com.i2mobi.net.NetUtil;
import com.maple.beautyjournal.PCDListActivity;
import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Address;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.ServerDataUtils;
import com.maple.beautyjournal.utils.SettingsUtil;
import com.maple.beautyjournal.widget.ProgressWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Add_View_AddressFragment extends BaseFragment implements ProgressWindow.OnDismissListener,
        View.OnTouchListener {
    private final int OPT_ADD = 0;
    private final int OPT_VIEW = 1;
    private final int OPT_UPDATE = 2;

    private int opt;
    EditText name, phone, post, address;
    TextView district, province, city;
    Button add_delete;
    ImageButton make_default;
    private Context context;
    private String errorMsg;
    private int id;
    private boolean isDefault = false;

    String addressList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String key = bundle.getString("opt");
        if (key.equals("add")) {
            opt = OPT_ADD;
        } else {
            opt = OPT_VIEW;
        }
        if (opt == OPT_VIEW) {
            id = bundle.getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_address_add, container, false);
        ImageView back = (ImageView) v.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finishPage();
            }

        });
        TextView title = (TextView) v.findViewById(R.id.title);
        if (opt == OPT_VIEW) {
            title.setText(getString(R.string.view_address));
        }
        context = getActivity();
        addressList = SettingsUtil.getAddressList(context);
        initView(v);
        v.setOnTouchListener(this);
        return v;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    private void initView(View parent) {
        district = (TextView) parent.findViewById(R.id.distinct);
        province = (TextView) parent.findViewById(R.id.province);
        city = (TextView) parent.findViewById(R.id.city);
        if (opt == OPT_ADD || opt == OPT_UPDATE) {
            district.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    showDistrictPicker("district");
                }

            });
            province.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    showDistrictPicker("province");
                }

            });
            city.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    showDistrictPicker("city");
                }

            });
        }
        name = (EditText) parent.findViewById(R.id.name);
        phone = (EditText) parent.findViewById(R.id.phone);
        post = (EditText) parent.findViewById(R.id.post);
        address = (EditText) parent.findViewById(R.id.address);
        if (opt == OPT_VIEW) {
            name.setEnabled(false);
            phone.setEnabled(false);
            post.setEnabled(false);
            address.setEnabled(false);
        }
        add_delete = (Button) parent.findViewById(R.id.add);
        if (opt == OPT_VIEW) {
            add_delete.setText(R.string.delete_address);
        } else {
            add_delete.setText(R.string.add_new_address);
        }
        add_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (opt == OPT_ADD) {
                    if (checkValidity()) { new AddAddressTask().execute(); }
                } else if (opt == OPT_VIEW) {
                    if (isDefault) {
                        Toast.makeText(context, getString(R.string.cannot_delete_default), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!TextUtils.isEmpty(addressList)) {
                        try {
                            JSONArray array = new JSONArray(addressList);
                            if (array.length() == 1) {
                                Toast.makeText(context, getString(R.string.cannot_delete_only_one), Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                        } catch (Exception e) {

                        }
                    }
                    new DeleteAddressTask().execute();
                }
            }
        });
        make_default = (ImageButton) parent.findViewById(R.id.make_default);
        if (opt == OPT_VIEW) {
            make_default.setVisibility(View.VISIBLE);
            make_default.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpdateAddressTask().execute();
                }
            });
        }
        if (ConstantsHelper.TEST && opt == OPT_ADD) {
            loadMockData();
        } else {
            loadAddressData();
        }
    }

    private void showDistrictPicker(String key) {
        Intent intent = new Intent(getActivity(), PCDListActivity.class);
        intent.putExtra("key", key);
        if (!TextUtils.isEmpty(province.getText().toString())) {
            intent.putExtra("province", province.getText().toString());
        }
        if (!TextUtils.isEmpty(city.getText().toString())) {
            intent.putExtra("city", city.getText().toString());
        }
        startActivityForResult(intent, 0);
    }

    private void loadAddressData() {
        if (!TextUtils.isEmpty(addressList)) {
            try {
                JSONArray array = new JSONArray(addressList);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Address addressItem = Address.fromJson(obj);
                    if (addressItem.id == id) {
                        name.setText(addressItem.name);
                        phone.setText(addressItem.phone);
                        address.setText(addressItem.address);
                        post.setText(addressItem.zip);
                        if (addressItem.isDefault) {
                            make_default.setVisibility(View.GONE);
                        }
                        province.setText(addressItem.province);
                        city.setText(addressItem.city);
                        district.setText(addressItem.district);
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMockData() {
        name.setText("TEST");
        phone.setText("13911112222");
        post.setText("100086");
        address.setText("TEST ADDRESS " + System.currentTimeMillis());
    }

    @Override
    public void onDismiss() {
        if (TextUtils.isEmpty(errorMsg)) {
            switch (opt) {
                case OPT_ADD:
                    Toast.makeText(context, getString(R.string.add_address_success), Toast.LENGTH_SHORT).show();
                    break;
                case OPT_VIEW:
                    Toast.makeText(context, getString(R.string.delete_address_success), Toast.LENGTH_SHORT).show();
                    break;
                case OPT_UPDATE:
                    Toast.makeText(context, getString(R.string.make_default_address_success), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            getFragmentManager().beginTransaction().remove(this).commit();
        } else {
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }


    private boolean hasAddress() {
        String text = SettingsUtil.getAddressList(context);
        if (!TextUtils.isEmpty(text)) {
            try {
                JSONArray array = new JSONArray(text);
                if (array.length() > 0) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private class AddAddressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url = NetUtil.getAddAddressUrl(context);
            NetUtil util = new HttpClientImplUtil(context, url);
            Map<String, String> addressMap = new HashMap<String, String>();
            addressMap.put("userid", SettingsUtil.getUserId(context));
            addressMap.put("username", SettingsUtil.getUserName(context));
            addressMap.put("phone", phone.getText().toString());
            addressMap.put("address", address.getText().toString());
            addressMap.put("zipcode", post.getText().toString());
            addressMap.put("buyer", name.getText().toString());
            addressMap.put("default", hasAddress() ? Integer.toString(0) : Integer.toString(1));
            addressMap.put("prov", province.getText().toString());
            addressMap.put("city", city.getText().toString());
            addressMap.put("district", district.getText().toString());
            util.setMap(addressMap);
            String result = util.doPost();
            Log.d(TAG, "add address result: " + result);
            errorMsg = null;
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    ServerDataUtils.getAddressListFromServer(context);
                } else {
                    errorMsg = obj.getString("info");
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismissProgress();
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            } else {
            	Toast.makeText(getActivity(), R.string.add_address_success, Toast.LENGTH_SHORT).show();
            	finishPage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    private void finishPage() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    private class DeleteAddressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url = NetUtil.getDeleteAddressURL(context);
            NetUtil util = new HttpClientImplUtil(context, url);
            Map<String, String> addressMap = new HashMap<String, String>();
            addressMap.put("userid", SettingsUtil.getUserId(context));
            addressMap.put("id", Integer.toString(id));
            for (String key : addressMap.keySet()) {
                Log.d(TAG, key + " : " + addressMap.get(key));
            }
            Log.d(TAG, "url is " + url);
            util.setMap(addressMap);
            String result = util.doPost();
            Log.d(TAG, "delete result : " + result);
            try {
                JSONObject obj = new JSONObject(result);
                if (ServerDataUtils.isTaskSuccess(obj)) {
                    errorMsg = null;
                    ServerDataUtils.getAddressListFromServer(context);
                } else {
                    errorMsg = obj.getString("info");
                }
            } catch (Exception e) {
                errorMsg = e.getLocalizedMessage();
                e.printStackTrace();
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
            showProgress();
        }
    }

    private class UpdateAddressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url = NetUtil.getUpdateAddressURL(context);
            NetUtil util = new HttpClientImplUtil(context, url);
            Map<String, String> updateAddressMap = new HashMap<String, String>();
            updateAddressMap.put("userid", SettingsUtil.getUserId(context));
            updateAddressMap.put("default", Integer.toString(1));
            updateAddressMap.put("id", Integer.toString(id));
            util.setMap(updateAddressMap);
            String result = util.doPost();
            Log.d(TAG, "update address, result is " + result);
            opt = OPT_UPDATE;
            try {
                JSONObject obj = new JSONObject(result);
                if (!ServerDataUtils.isTaskSuccess(obj)) {
                    errorMsg = obj.getString("info");
                } else {
                    ServerDataUtils.getAddressListFromServer(context);
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
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }
    }

    private boolean checkValidity() {
        if (name.length() == 0) {
            Toast.makeText(context, getString(R.string.error_name_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.length() == 0) {
            Toast.makeText(context, getString(R.string.error_address_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.length() == 0) {
            Toast.makeText(context, getString(R.string.error_phone_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle extra = data.getExtras();
            if (extra.keySet().contains("province")) {
                if (province.getText() != null && !province.getText().toString()
                        .contentEquals(extra.getString("province"))) {
                    //clean city and district
                    city.setText("");
                    district.setText("");
                }
                province.setText(extra.getString("province"));
            }
            if (extra.keySet().contains("city")) {
                if (city.getText() != null && !city.getText().toString().contentEquals(extra.getString("city"))) {
                    district.setText("");
                }
                city.setText(extra.getString("city"));
            }
            if (extra.keySet().contains("district")) {
                district.setText(extra.getString("district"));
            }
        }
    }
}
