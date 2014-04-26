package com.maple.beautyjournal.entitiy;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tian on 13-7-21.
 */
public class Address {
    public int id = -1;
    public String phone;
    public String zip;
    public String address;
    public String name;
    public String province;
    public String city;
    public String district;
    public boolean isDefault = false;

    public static Address fromJson(JSONObject obj) {
        Address addr = new Address();
        addr.id = obj.optInt("id");
        addr.phone = obj.optString("phone");
        addr.address = obj.optString("address");
        addr.zip = obj.optString("zipcode");
        addr.isDefault = obj.optInt("default", 0) == 1;
        addr.name = obj.optString("buyer");
        addr.province = obj.optString("prov");
        addr.city = obj.optString("city");
        addr.district = obj.optString("district");
        return addr;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        if (id != -1) {
            obj.put("id", id);
        }
        obj.put("phone", phone);
        obj.put("address", address);
        obj.put("zipcode", zip);
        obj.put("default", isDefault ? 1 : 0);
        obj.put("buyer", name);
        obj.put("province", province);
        obj.put("city", city);
        obj.put("district", "district");
        return obj;
    }
}
