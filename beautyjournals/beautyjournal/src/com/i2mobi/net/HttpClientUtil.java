package com.i2mobi.net;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public abstract class HttpClientUtil extends NetUtil{
	
	protected HttpClient client;
	
	protected HttpClientUtil(Context context,Map<String, String> map, String path) {
		super(context,map, path);
		
	}
     
	protected HttpClientUtil(Context context,String path) {
		super(context,path);
	}
	
	protected HttpResponse postClient(HttpEntity entity) throws IOException{
		client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpPost request = new HttpPost(path);
		request.setEntity(entity); 
		if (isCookie)
			request.setHeader("Cookie", cookie);
		HttpResponse response = client.execute(request);
		return response;
	}
	
	protected HttpResponse getClient(String uri) throws IOException{
		client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);   //连接超时 5秒
		HttpGet request = new HttpGet(uri);
		if (isCookie)
			request.setHeader("Cookie", cookie);
		HttpResponse response = client.execute(request);
		return response;
	}
	
	
	public List<Cookie> getCookies(){
		DefaultHttpClient defaultHttpClient = (DefaultHttpClient) client;
		if (defaultHttpClient == null)
			return null;
		else 
			return defaultHttpClient.getCookieStore().getCookies();
	}
	public String convertStreamToString(InputStream is) {    
        /* 
          * To convert the InputStream to String we use the BufferedReader.readLine() 
          * method. We iterate until the BufferedReader return null which means 
          * there's no more data to read. Each line will appended to a StringBuilder 
          * and returned as String. 
          */   
         BufferedReader reader = new BufferedReader(new InputStreamReader(is));    
         StringBuilder sb = new StringBuilder();    
   
         String line = null;    
        try {    
            while ((line = reader.readLine()) != null) {    
                 sb.append(line + "\n");    
             }    
         } catch (IOException e) {    
             e.printStackTrace();    
         } finally {    
            try {    
                 is.close();    
             } catch (IOException e) {    
                 e.printStackTrace();    
             }    
         }    
   
        return sb.toString();    
     }    

}
