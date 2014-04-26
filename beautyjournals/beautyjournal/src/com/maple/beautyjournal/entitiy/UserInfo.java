package com.maple.beautyjournal.entitiy;

import com.maple.beautyjournal.utils.AuthUtils.AuthType;

public class UserInfo {

	public String id;
    public String name;
    public String passwd;
	public String realName;
	public int gender;

	public String image;
	public String imagename;
	public String imageUrl;

	public String phone;
	public String email;	
	public String platform;
	public String address;
	public String zipcode;


	public static class ThirdPartyUserInfo{
		private AuthType authType;
		private com.sina.weibo.sdk.auth.Oauth2AccessToken accessToken;
		private int uid;
	}
}
