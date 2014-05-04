package com.i2mobi.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.maple.beautyjournal.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpClientImplUtil extends HttpClientUtil {

    public HttpClientImplUtil(Context context, String path) {
        super(context, path);
    }

    public HttpClientImplUtil(Context context, Map<String, String> map, String path) {
        super(context, map, path);
    }

    @Override
    public String doGet() {
        long begin = System.currentTimeMillis();
        String result = "";
        StringBuilder sb = new StringBuilder(path);
        try {
            if (map != null && !map.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }

                sb = sb.deleteCharAt(sb.length() - 1);
            }
            String str = sb.toString();
            Log.d("XXX","-------"+str);
            Log.e("request url", str);
            HttpResponse response = getClient(str);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = response.getEntity().getContent();
                result = getResult(inputStream);
            }
        } catch (UnsupportedEncodingException e) {
            result = "{'status':'error','info':'"+ENCODING_ERROR+"'}";
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            result = "{'status':'error','info':'"+HTTP_ERROR+"'}";
            e.printStackTrace();
        } catch (IOException e) {
            result = "{'status':'error','info':'"+HTTP_IO_ERROR+"'}";
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

    @Override
    public String doPost() {
        long begin = System.currentTimeMillis();
        String result = "";
        try {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    NameValuePair nameValuePairs = new BasicNameValuePair(entry.getKey(), entry.getValue());
                    parameters.add(nameValuePairs);
                }
            }
            HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
            HttpEntity entity2 = new UrlEncodedFormEntity(parameters, "UTF-8");
            Log.d("XXX", "doPost, url is " + path);
            InputStream is = entity2.getContent();
            byte[]data = new byte[is.available()];
            is.read(data);
            Log.d("HttpUtils", "doPost, content is " + new String(data));
            HttpResponse response = postClient(entity);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = response.getEntity().getContent();
                result = getResult(inputStream);
            } else {
                Log.d(this.getClass().getName(), "response is " + response.getStatusLine().getStatusCode());
                InputStream inputStream = response.getEntity().getContent();
                result = getResult(inputStream);
                Log.d(this.getClass().getName(), "result is " + result);

            }
        } catch (UnsupportedEncodingException e) {
            result = "{'status':'error','info':'"+ENCODING_ERROR+"'}";
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            result = "{'status':'error','info':'"+HTTP_ERROR+"'}";
            e.printStackTrace();
        } catch (IOException e) {
            result = "{'status':'error','info':'"+HTTP_IO_ERROR+"'}";
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

    @Override
    public String doGet(Map<String, String[]> maps) {
        long begin = System.currentTimeMillis();
        String result = "";
        StringBuilder sb = new StringBuilder(path);
        try {
            if (maps != null && !maps.isEmpty()) {
                sb.append("?");
                for (Entry<String, String[]> entry : maps.entrySet()) {
                    int length = entry.getValue().length;
                    for (int i = 0; i < length; i++) {
                        sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue()[i], "utf-8"))
                                .append("&");
                    }
                }
                sb = sb.deleteCharAt(sb.length() - 1);
            }
            String str = sb.toString();
            HttpResponse response = getClient(str);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = response.getEntity().getContent();
                result = getResult(inputStream);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            result = HTTP_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            result = HTTP_IO_ERROR;
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

    @Override
    public String doPost(Map<String, String[]> maps) {
        long begin = System.currentTimeMillis();
        String result = "";
        try {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            if (maps != null && !maps.isEmpty()) {
                for (Entry<String, String[]> entry : maps.entrySet()) {
                    int length = entry.getValue().length;
                    for (int i = 0; i < length; i++) {
                        NameValuePair nameValuePairs = new BasicNameValuePair(entry.getKey(), entry.getValue()[i]);
                        parameters.add(nameValuePairs);
                    }
                }
            }
            HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
            HttpResponse response = postClient(entity);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = response.getEntity().getContent();
                result = getResult(inputStream);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            result = HTTP_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            result = HTTP_IO_ERROR;
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

    @Override
    public String uploadFile(String filePath) {
        long begin = System.currentTimeMillis();
        String result = "";
        File file = new File(filePath);
        FileBody bin = null;
        if (file != null) { bin = new FileBody(file); }
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", bin);
        try {
            HttpResponse response = postClient(reqEntity);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    InputStream in = resEntity.getContent();
                    result = getResult(in);
                    resEntity.consumeContent();
                }
            }
        } catch (ClientProtocolException e) {
            result = HTTP_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            result = HTTP_IO_ERROR;
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }

        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

    @Override
    public String downFile(String savePath) {
        long begin = System.currentTimeMillis();
        String result = "";
        StringBuilder sb = new StringBuilder(path);
        try {
            if (map != null && !map.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }
                sb = sb.deleteCharAt(sb.length() - 1);
            }
            String str = sb.toString();
            HttpResponse response = getClient(str);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = response.getEntity().getContent();
                boolean flag = saveFile(savePath, inputStream);
                result = flag ? savePath : "";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            result = HTTP_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            result = HTTP_IO_ERROR;
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

    @Override
    public String uploadMultiPart() {
        long begin = System.currentTimeMillis();
        String result = "";

        try {
            MultipartEntity reqEntity = new MultipartEntity();
            if (fileEntities != null) {
                for (Map.Entry<String, File> entry : fileEntities.entrySet()) {
                    Log.d(this.getClass().getName(), "add part for : " + entry.getKey() + ", " + entry.getValue()
                            .getName() + ", " + entry.getValue().exists());
                    reqEntity.addPart(entry.getKey(), new ByteArrayBody(Utils.getAvatarFileBytes(context), null));
                }
            }
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    Log.d(this.getClass().getName(), "add part for : " + entry.getKey() + ", " + entry.getValue());
                    reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("utf-8")));
                }
            }
            HttpResponse response = postClient(reqEntity);

            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    InputStream in = resEntity.getContent();
                    result = getResult(in);
                    resEntity.consumeContent();
                }
            }
        } catch (ClientProtocolException e) {
            result = HTTP_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            result = HTTP_IO_ERROR;
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }

        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

    @Override
    public Bitmap doGetBitmap() {
        long begin = System.currentTimeMillis();
        Bitmap result = null;
        StringBuilder sb = new StringBuilder(path);
        try {
            if (map != null && !map.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }

                sb = sb.deleteCharAt(sb.length() - 1);
            }
            String str = sb.toString();
            HttpResponse response = getClient(str);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = response.getEntity().getContent();
                result = getBitmap(inputStream);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.getConnectionManager().shutdown();
        }
        long end = System.currentTimeMillis();
        test = path + " 执行时间：" + (end - begin);
        return result;
    }

}
