package com.i2mobi.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.maple.beautyjournal.utils.SettingsUtil;

import org.apache.http.cookie.Cookie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class NetUtil implements URLConstant, HttpConstant {
    protected String path;
    protected Map<String, String> map = null;
    protected String filePath;
    protected String cookie;
    protected Context context;
    protected boolean isCookie = true;
    protected static String test = "";
    protected Map<String, File> fileEntities;

    public NetUtil(Context context, Map<String, String> map, String path) {
        init(context, map, path);
    }


    public NetUtil(Context context, String path) {
        init(context, map, path);
    }

    /**
     * 初始化
     *
     * @param context
     * @param map
     * @param path
     */
    private void init(Context context, Map<String, String> map, String path) {
        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
        String cookieName = manager.getString("cookie_name", "");
        String cookieValue = manager.getString("cookie_value", "");
        String userAccount = manager.getString("userAccount", "");
        this.context = context;
        if (path.startsWith("http:") || path.startsWith("https:")) {  //兼容http和https
            this.path = path;
        } else {
            this.path = SERVER_ADDRESS + path;
        }
        if (map != null) { this.map = map; }
        cookie = "userAccount=" + userAccount + ";" + cookieName + "=" + cookieValue;
        Log.e("cookie", cookie);
    }


    public Bitmap getBitmap(InputStream inputStream) {
        Bitmap result = null;
        if (inputStream == null) { return result; }
        try {
            return result = BitmapFactory.decodeStream(inputStream);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getResult(InputStream inputStream) {
        String result = "";
        if (inputStream == null) { return result; }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                result += line;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = HTTP_IO_ERROR;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                result = HTTP_IO_ERROR;
            }
        }
        return result;
    }


    /**
     * 保存文件
     *
     * @param savePath
     * @param inputStream
     * @return
     */
    public boolean saveFile(String savePath, InputStream inputStream) {
        boolean flag = false;
        if (inputStream == null) { return flag; }
        File storeFile = new File(savePath);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(storeFile);
            byte[] buf = new byte[1024];
            int ch = -1;
            while ((ch = inputStream.read(buf)) != -1) {
                output.write(buf, 0, ch);
            }
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = false;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (inputStream != null) { inputStream.close(); }
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    public abstract String doGet();

    public abstract String doPost();

    public abstract Bitmap doGetBitmap();

    public abstract String doGet(Map<String, String[]> maps);

    public abstract String doPost(Map<String, String[]> maps);

    public void setFileEntitiy(String key, File file) {
        if (fileEntities == null) {
            fileEntities = new HashMap<String, File>();
        }
        fileEntities.put(key, file);
    }

    /**
     * @param filePath 本地文件路径
     * @return
     */
    public abstract String uploadFile(String filePath);


    /**
     * @param savePath 保存的文件路径
     * @return
     */
    public abstract String downFile(String savePath);

    /**
     *
     */
    public abstract String uploadMultiPart();

    /**
     * 得到cookie
     *
     * @return
     */
    public abstract List<Cookie> getCookies();


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isCookie() {
        return isCookie;
    }

    public void setCookie(boolean isCookie) {
        this.isCookie = isCookie;
    }

    private static String APP_ID;
    //得到appId，应用id号
    public static String getAppID(Context context) {
        if (!TextUtils.isEmpty(APP_ID)) {
            return APP_ID;
        }
        StringBuilder sb = new StringBuilder();
        final String SEPARATOR = "_";
        sb.append(Build.VERSION.RELEASE);
        sb.append(SEPARATOR);
        sb.append(Build.VERSION.SDK_INT);
        sb.append(SEPARATOR);
        sb.append(Build.MANUFACTURER);
        sb.append(SEPARATOR);
        sb.append(Build.MODEL);
        sb.append(SEPARATOR);
        String secureId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        sb.append(secureId);
        try {
            APP_ID = URLEncoder.encode(sb.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            APP_ID = secureId;
        }
        return APP_ID;
    }

    public static String getArticleListUrl(Context context, int category, int offset, int[] args) {
        String appId = getAppID(context);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length - 1) {
                sb.append(".");
            }
        }
        String param = sb.toString();
        return String.format(ARTICLE_LIST_URL, category, appId, offset, param);
    }

    public static String getProductListUrl(Context context, int category, int offset, int orderby) {
        String appId = getAppID(context);
        return String.format(PRODUCT_LIST_URL, category, appId, offset, orderby);
    }

    public static String getArticleContentUrl(Context context, String id) {
        return String.format(ARTICLE_CONTENT_URL, getAppID(context), id);
    }

    public static String getRegisterUrl(Context context) {
        String appId = getAppID(context);
        return String.format(REGISTER_URL, appId);
    }

    public static String getLogInUrl(Context context, String username, String password) {
        String appId = getAppID(context);
        return String.format(LOGIN_URL, username, password, appId);
    }

    public static String getArticleDetailUrl(Context context, String itemId) {
        String appId = getAppID(context);
        String url = String.format(ARTICLE_DETAIL_URL, itemId, appId);
        if (SettingsUtil.isLoggedIn(context)) {
            url = url + "/userid/" + SettingsUtil.getUserId(context);
        }
        return url;
    }

    public static String getAdUrl(Context context, int max) {
        String appId = getAppID(context);
        return String.format(AD_URL, Integer.toString(max), appId);
    }
    public static String getArticleCommentUrl(Context context,String articleId,int size){
        return String.format(ARTICLE_COMMENT,articleId,Integer.toString(size));
    }
    public static String getHotWords(Context context,int num){
        String appId = getAppID(context);
        return String.format(HotWords_URL, Integer.toString(num));
    }
    public static String getUserIsUniqueUrl(Context context, String username){
    	return String.format(USER_ISUNIQUE_URL,username);

    }
    
    public static String getLikeArticleUrl(Context context, String id) {
        String appId = getAppID(context);
        return String.format(LIKE_ARTICLE_URL, id, appId);
    }

    public static String getLikeProductUrl(Context context, String id) {
        String appId = getAppID(context);
        return String.format(LIKE_PRODUCT_URL, id, appId);
    }

    public static String getFavArticleUrl(Context context, String id) {
        String appId = getAppID(context);
        return String.format(FAV_ARTICLE_URL, SettingsUtil.getUserId(context), id, appId);
    }

    public static String getFavProductUrl(Context context, String id) {
        String appId = getAppID(context);
        return String.format(FAV_PRODUCT_URL, SettingsUtil.getUserId(context), id, appId);
    }

    public static String getCancelFavArticleUrl(Context context, String id) {
        String appId = getAppID(context);
        return String.format(CANCEL_FAV_ARTICLE_URL, SettingsUtil.getUserId(context), id, appId);
    }

    public static String getCancelFavProductUrl(Context context, String id) {
        String appId = getAppID(context);
        return String.format(CANCEL_FAV_PRODUCT_URL, SettingsUtil.getUserId(context), id, appId);
    }

    public static String getFavArticleListUrl(Context context) {
        String appId = getAppID(context);
        return String.format(FAV_ARTICLE_LIST_URL, SettingsUtil.getUserId(context), appId);
    }

    public static String getFavProductListUrl(Context context) {
        String appId = getAppID(context);
        return String.format(FAV_PRODUCT_LIST_URL, SettingsUtil.getUserId(context), appId);
    }

    public static String getProductDetailUrl(Context context, String id) {
        String appId = getAppID(context);
        String url = String.format(PRODUCT_DETAIL_URL, id, appId);
        if (SettingsUtil.isLoggedIn(context)) {
            url = url + "/userid/" + SettingsUtil.getUserId(context);
        }
        return url;
    }

    public static String getArticleListUrl2(Context context, int category, int page, int size) {
        String appId = getAppID(context);
        return String.format(ARTICLE_LIST_URL2, category, page, size);
    }

    public static String getProductListUrl2(Context context, String listBy, String orderBy, int size, int offset) {
        return String.format(PRODUCT_LIST_URL2, listBy, orderBy, size, offset);
    }

    public static String getProductCommentsUrl(Context context, String productId, int size, int offset) {
        return String.format(PRODUCT_COMMENTS_URL, productId, size, offset);
    }

    public static String getUpdateInfoUrl(Context context) {
        return String.format(UPDATE_INFO_URL, getAppID(context));
    }

    public static String getSubmitCommentUrl(Context context) {
        return String.format(SUBMIT_COMMENT_URL, getAppID(context));
    }

    public static String getAddAddressUrl(Context context) {
        return String.format(ADD_ADDRESS_URL, getAppID(context));
    }

    public static String getDeleteAddressURL(Context context) {
        return String.format(REMOVE_ADDRESS_URL, getAppID(context));
    }

    public static String getUpdateAddressURL(Context context) {
        return String.format(UPDATE_ADDRESS_URL, getAppID(context));
    }

    public static String getQueryAddressURL(Context context) {
        return String.format(QUERY_ADDRESS_URL, SettingsUtil.getUserId(context), getAppID(context));
    }

    public static String getOrderListUrl(Context context, int size, int offset) {
        return String.format(ORDER_LIST_URL, SettingsUtil.getUserName(context), size, offset);
    }

    public static String getOrderDetailUrl(String orderId) {
        return String.format(ORDER_ID_URL, orderId);
    }

    public static String getCreateOrderUrl() {
        return CREATE_ORDER_URL;
    }

    public static String getFeedbackUrl() {
        return FEEDBACK_URL;
    }

    //获得搜索的url
    public static String getSearchUrl(Context context){
        //String appId = getAppID(context);
        return ARTICLE_SEARCH_URL;
    }
}
