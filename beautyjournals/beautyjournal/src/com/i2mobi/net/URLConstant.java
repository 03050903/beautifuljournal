
package com.i2mobi.net;

public interface URLConstant {
    /** 测试 */
    boolean TEST = false;
    /** 发布 */
    // boolean TEST = false;
    /** 服务器IP */
    String SERVER_ADDRESS = "http://42.96.185.57:8007/";

    //AD
    String AD_URL = "index.php?r=app/rec/index/max_cnt/%s/appid/%s/";

    //HotWords
    String HotWords_URL="index.php?r=app/search/hotWords/num/%s/";

    //Article
    String ARTICLE_LIST_URL = "index.php?r=app/article/list/cat/%d/appid/%s/offset/%d/order/%s";
    String ARTICLE_CONTENT_URL = "index.php?r=app/article/view/item_id/%s/appid/%s";
    String ARTICLE_DETAIL_URL = "index.php?r=app/article/view/item_id/%s/appid/%s";
    String LIKE_ARTICLE_URL = "index.php?r=app/article/like/id/%s/appid/%s/";
    String FAV_ARTICLE_URL = "index.php?r=app/article/favorite/userid/%s/id/%s/appid/%s/";
    String ARTICLE_LIST_URL2 = "index.php?r=app/article/list/cat/%d/page/%d/size/%d";
    String CANCEL_FAV_ARTICLE_URL = "index.php?r=app/article/favoritedel/userid/%s/id/%s/appid/%s/";
    String FAV_ARTICLE_LIST_URL = "index.php?r=app/article/favoritelist/userid/%s/appid/%s/";

    //Product
    String PRODUCT_LIST_URL = "index.php?r=app/product/list/cat/%d/appid/%s/offset/%d/order/%s";
    String PRODUCT_DETAIL_URL = "index.php?r=app/product/view/id/%s/appid/%s";
    String FAV_PRODUCT_URL = "index.php?r=app/product/favorite/userid/%s/id/%s/appid/%s/";    
    String CANCEL_FAV_PRODUCT_URL = "index.php?r=app/product/favoritedel/userid/%s/id/%s/appid/%s/";
    String FAV_PRODUCT_LIST_URL = "index.php?r=app/product/favoritelist/userid/%s/appid/%s/";    
    String PRODUCT_LIST_URL2 = "index.php?r=app/product/%s/sort/%s/size/%d/offset/%d";
    String CATEGORY_LIST_URL = "index.php?r=app/product/getcategorys";
    String FUNCTION_LIST_URL = "index.php?r=app/product/getfunctions";
    String BRAND_LIST_URL = "index.php?r=app/product/getbrands";
    String LIKE_PRODUCT_URL = "index.php?r=app/product/like/id/%s/appid/%s/";
    
    //Comment
    String PRODUCT_COMMENTS_URL = "index.php?r=app/comment/getlistbypro/proid/%s/size/%d/offset/%d";
    String SUBMIT_COMMENT_URL = "index.php?r=app/comment/submit/appid/%s";

    //User
    String REGISTER_URL = "index.php?r=app/user/add/appid/%s";
    String LOGIN_URL = "index.php?r=app/user/valid/username/%s/passwd/%s/appid/%s";
    String UPDATE_INFO_URL = "index.php?r=app/user/updateinfo/appid/%s";
    String USER_ISUNIQUE_URL="index.php?r=app/user/isuniquename/username/%s";
    String ADD_ADDRESS_URL = "index.php?r=app/user/addextrainfo/appid/%s";
    String REMOVE_ADDRESS_URL = "index.php?r=app/user/delextrainfo/appid/%s";
    String UPDATE_ADDRESS_URL = "index.php?r=app/user/upextrainfo/appid/%s";
    String QUERY_ADDRESS_URL = "index.php?r=app/user/getextrainfo/userid/%s/appid/%s";
    String UPDATE_USER_PASSWD_URL ="index.php?r=app/user/resetpasswd";

    //Order
    String ORDER_LIST_URL = "index.php?r=app/order/list/username/%s/size/%d/offset/%d";
    String ORDER_ID_URL = "index.php?r=app/order/view/orderid/%s";
    String CREATE_ORDER_URL = "index.php?r=app/order/create";
    
    //FeedBack
    String FEEDBACK_URL = "index.php?r=app/suggest/add";
    
    //MsgCode
    String MESSAGECODE_GET_URL = "index.php?r=app/identify/getcode/type/%s/phone/%s";
    String MESSAGECODE_VERIFY_URL = "index.php?r=app/identify/verifycode/phone/%s/code/%s";
    
    //AppUtils  Notifiations and versions
    String NOTIFICATION_CHECK_URL = "index?r=app/notification/recentNotification";
    String VERSION_CHECK_URL="index?r=app/version/newestVersion";
}
