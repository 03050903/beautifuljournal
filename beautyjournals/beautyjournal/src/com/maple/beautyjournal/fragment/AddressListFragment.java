package com.maple.beautyjournal.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.maple.beautyjournal.R;
import com.maple.beautyjournal.base.BaseFragment;
import com.maple.beautyjournal.entitiy.Address;
import com.maple.beautyjournal.utils.ConstantsHelper;
import com.maple.beautyjournal.utils.SettingsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddressListFragment extends BaseFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences pref;
    int addressCount = 0;
    private View listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        pref = activity.getSharedPreferences(SettingsUtil.PREF_NAME, Context.MODE_PRIVATE);
        if (listener == null) {
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    Log.d(TAG, "onSharedPreferenceChanged, key is " + key);
                    if (key != null && key.contentEquals(SettingsUtil.ADDRESS_LIST)) {
                        reloadData();
                    }
                }
            };
        }
        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    private void reloadData() {
        loadAddresses(listView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (pref != null && listener != null) {
            pref.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_address_list, container, false);
        listView = v;
        ImageView back = (ImageView) v.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }

        });

        Button add = (Button) v.findViewById(R.id.add_address);
        add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (addressCount == ConstantsHelper.MAX_ADDRESS_COUNT) {
                    Toast.makeText(getActivity(), String
                            .format(getString(R.string.max_address_reached), ConstantsHelper.MAX_ADDRESS_COUNT), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
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

        });
        loadAddresses(v);
        return v;
    }

    private void loadAddresses(View v) {
        clearAddressList(v);
        addressCount = 0;
        String addressListString = SettingsUtil.getAddressList(getActivity());
        if (!TextUtils.isEmpty(addressListString)) {
            try {
                JSONArray addressArray = new JSONArray(addressListString);
                if (addressArray.length() > 0) {

                    for (int i = 0; i < addressArray.length(); i++) {
                        JSONObject obj = addressArray.getJSONObject(i);
                        Address address = Address.fromJson(obj);
                        bindView(v, address, i);
                        Log.d(TAG, "add address " + address.name + " , " + address.address);
                        addressCount++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindView(View v, Address address, int position) {
        TextView tv = null;
        switch (position) {
            case 0:
                tv = (TextView) v.findViewById(R.id.address1);
                break;
            case 1:
                tv = (TextView) v.findViewById(R.id.address2);
                break;
            case 2:
                tv = (TextView) v.findViewById(R.id.address3);
                break;
        }
        if (tv != null) {
            tv.setText(formatAddress(address));
            tv.setVisibility(View.VISIBLE);
            tv.setTag(address);
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    bundle.putString("opt", "view");
                    Address address = (Address) v.getTag();
                    bundle.putInt("id", address.id);
                    Fragment add_address = Fragment
                            .instantiate(getActivity(), Add_View_AddressFragment.class.getName(), bundle);
                    ft.add(R.id.realtabcontent, add_address);
                    ft.commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                }
            });
            if (address.isDefault) {
                tv.setTextColor(getResources().getColor(R.color.default_pink_color));
                tv.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
                        .getDrawable(R.drawable.default_address_check), null);
            } else {
                tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        }
    }

    private String formatAddress(Address address) {
        return String.format(getString(R.string.address_list_text), address.name, address.address);
    }

    private void clearAddressList(View v) {
        v.findViewById(R.id.address1).setVisibility(View.GONE);
        v.findViewById(R.id.address2).setVisibility(View.GONE);
        v.findViewById(R.id.address3).setVisibility(View.GONE);
    }
}
