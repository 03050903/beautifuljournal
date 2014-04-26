package com.basemaple.utils;

import java.util.ArrayList;

public class EventChain {
	
	public static final String INTENT_KEY="EVENT_CHAIN_KEY";

	//NAVI
	public static final String EVENT_NAV_CLICKKEY="NAVI";
	
	//CHANNEL
	public static final String EVENT_CHANNEL_CLICKKEY="CHANNEL";
	
	//ARTICLE
	public static final String EVENT_ARTICLE_KEY="ARTICLE";
	public static final String EVENT_ARTICLE_SRCKEY="ARTICLESRC";
	public static final String ARTICLE_SRC_ARTICLE_LIST="ARTICLE_LIST";
	public static final String ARTICLE_SRC_ARTICLE_FAVLIST="ARTICLE_FAV_LIST";
	
	//PRODUCT
	public static final String EVENT_PRODUCT_KEY="PRODUCT";
	public static final String EVENT_PRODUCT_SRCKEY="PRODUCTSRC";
	public static final String PRODUCT_SRC_ARTICLE_HYPELINK="ARTICLE_HYPERLINK";
	public static final String PRODUCT_SRC_ARTICLE_RELATED_PRODUCT="ARTICLE_RELATEDPROD";
	public static final String PRODUCT_SRC_PRODUCT_LIST="PRODUCT_LIST";
	public static final String PRODUCT_SRC_PRODUCT_FAVLIST="PRODUCT_FAV_LIST";
	
	//CART
	public static final String EVENT_CARTADD_KEY="CARTADD";
	
	//ORDER
	public static final String EVENT_ORDER_SUBMIT_KEY="ORDER_SUBMIT";
	
	//PAY
	public static final String EVENT_PAY_KEY="PAY";
	
	
	private ArrayList<String> mEventChains= new ArrayList<String>();

	public ArrayList<String> getEventChains() {
		return mEventChains;
	}

	public void AddEvent(String key, String val){
		
	}
}
