package com.martoff.esmart.api_calls;

import android.util.Log;

import com.martoff.esmart.extras.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Safeer on 14-May-16.
 */
public class WebServices {
    public static String TAG = "WebServices";

    public static String BASE_URL = "http://rest.esmart.by/";
    public static String BASE_URL_IMAGE = "http://dev-by.esmart.by/gi/";

    public static String INITIALIZING_CALL = BASE_URL + "init";

    public static String CATEGORIES_RESPONSE(JSONObject jsonObject) {
        int type, menu_id, group_id;
        try {
            type = jsonObject.getInt(Constants.MENU_TYPE);
            menu_id = jsonObject.getInt(Constants.MENU_ID);
            group_id = jsonObject.getInt(Constants.GROUP_ID);
            return BASE_URL + "categories/get/" + type + "/" + menu_id + "/" + group_id;
        } catch (JSONException e) {
            Log.e(TAG, "JSONException :::: " + e);
        }
        return null;
    }


    public static String RECENT_ITEMS_URL(int user_id) {
        return BASE_URL + "user/home-recents/" + user_id;
    }

    public static String RECENT_SEARCHES_URL(int user_id) {
//        http://rest.esmart.by/user/home-searches/2
        return BASE_URL + "user/home-searches/" + user_id;
    }

    public static String SEARCH_URL(String text_to_search, int menu_id) {
//http://rest.esmart.by/search/?q=[air]&r=[1]
        return BASE_URL + "search/?q=" + text_to_search + "&r=" + menu_id;
    }

    public static String IMAGE_URL(int width_of_image, int heigh_of_image, String image_name) {
        return BASE_URL_IMAGE + "p/" + width_of_image + "x" + heigh_of_image + "/" + image_name;
    }
}
