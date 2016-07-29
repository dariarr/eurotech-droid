package com.martoff.esmart.api_calls;

import android.util.Log;

import com.martoff.esmart.AppController;
import com.martoff.esmart.extras.Constants;
import com.martoff.esmart.pojo.CategoriesResponse;
import com.martoff.esmart.pojo.InitResponse;
import com.martoff.esmart.pojo.ItemRecentSearches;
import com.martoff.esmart.pojo.RecentItemsResponse;
import com.martoff.esmart.pojo.ResponseMods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Safeer on 15-May-16.
 */
public class WebRequests {
    static String TAG = "WebRequests";

    //this method is for getting toolbar items. e.g. PRODUCTS, ELECTRONICS, HEATING, AIR & COOL etc...
    public static InitResponse postDataToolbarItems(String data, String url) {

        Log.e(TAG, "TOOLBARS DATA = " + data);
        Log.e(TAG, "TOOLBARS URL = " + url);

        StringBuilder response = new StringBuilder();
        InitResponse server_Init_response = new InitResponse();
        JSONObject jsonObject = null;
        URL obj = null;

        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-ESM-KEY", Constants.API_KEY);
            con.setRequestProperty("X-ESM-LID", AppController.APP_LANGUAGE);
//            con.setRequestProperty("X-ESM-DID", "1"); //this will be always 1, as told by misha
            con.setRequestProperty("Content-Type", "application/json");
            // For POST only - START
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(data.getBytes("UTF-8"));
            os.flush();
            os.close();
            // For POST only - END
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) { //success

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.e(TAG, "response :: " + response);
                JSONObject json_object = new JSONObject(response.toString());

                server_Init_response.setUid(json_object.getInt("uid"));
                server_Init_response.setNotifs(json_object.getString("notifs"));

                ArrayList<ResponseMods> responseModsList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(json_object.getString("mods"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);

                    ResponseMods responseMods = new ResponseMods();

                    int type = jsonobject.getInt("type");
                    responseMods.setType(type);

                    int menu_id = jsonobject.getInt("menu_id");
                    responseMods.setMenu_id(menu_id);

                    int group_id = jsonobject.getInt("group_id");
                    responseMods.setGroup_id(group_id);

                    int cat_id = jsonobject.getInt("cat_id");
                    responseMods.setCat_id(cat_id);

                    String label = jsonobject.getString("label");
                    responseMods.setLabel(label);

                    int count = jsonobject.getInt("count");
                    responseMods.setCount(count);

                    responseModsList.add(responseMods);
                }
                server_Init_response.setResponse_mods(responseModsList);
                return server_Init_response;
            }
        } catch (ConnectException e) {
            Log.e(TAG, "Connection Exception :: " + e);
        } catch (Exception e) {
            Log.e(TAG, "General Exception :: " + e);
        }
        return null;
    }


    //this method is for getting sub-categories on products fragment
    public static ArrayList<CategoriesResponse> getSubCategories(String url) {

        Log.e(TAG, "ITEMS = " + url);

        StringBuilder response = new StringBuilder();
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-ESM-KEY", Constants.API_KEY);
            con.setRequestProperty("X-ESM-LID", AppController.APP_LANGUAGE);

            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) { //success

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.e(TAG, "response :: " + response);
                JSONArray json_array = new JSONArray(response.toString());
                ArrayList<CategoriesResponse> prod_sub_cat_list = new ArrayList<>();
                CategoriesResponse prodCategories;

                //response single item ::[{"menu_id":1,"group_id":0,"cat_id":0,"label":"Automotive","count":56}]
                Log.e(TAG, "json_array :: " + json_array.length());
                for (int i = 0; i < json_array.length(); i++) {

                    JSONObject jsonObject = json_array.getJSONObject(i);
//                    Log.e(TAG, "json object at index " + i + " ::" + jsonObject);
                    prodCategories = new CategoriesResponse();

                    prodCategories.setMenu_id(jsonObject.getInt(Constants.MENU_ID));
                    prodCategories.setGroup_id(jsonObject.getInt(Constants.GROUP_ID));
                    prodCategories.setCat_id(jsonObject.getInt(Constants.CAT_ID));
                    prodCategories.setLabel(jsonObject.getString(Constants.LABEL));
                    prodCategories.setCount(jsonObject.getInt(Constants.COUNT));

                    prod_sub_cat_list.add(prodCategories);
                }
                return prod_sub_cat_list;
            }
        } catch (ConnectException e) {
            Log.e(TAG, "CHECKPOINT: Connection Exception :: " + e);
        } catch (Exception e) {
            Log.e(TAG, "CHECKPOINT: General Exception :: " + e);
        }
        return null;
    }


    //this method is for getting search results
    public static ArrayList<String> getSearchResults(String url) {

        Log.e(TAG, "SEARCH URL :: " + url);

        StringBuilder response = new StringBuilder();
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-ESM-KEY", Constants.API_KEY);
            con.setRequestProperty("X-ESM-LID", AppController.APP_LANGUAGE);
            con.setRequestProperty("X-ESM-AT", Constants.AUTHORIZATION_TOKEN);

            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) { //success

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.e(TAG, "SEARCH QUERY response :: " + response);

                return null;
            } else {
                Log.e(TAG, "SERVER NOT AVAILABLE");
            }
        } catch (ConnectException e) {
            Log.e(TAG, "CHECKPOINT: Connection Exception :: " + e);
        } catch (Exception e) {
            Log.e(TAG, "CHECKPOINT: General Exception :: " + e);
        }
        return null;
    }


    //this method is for getting recent items
    public static ArrayList<RecentItemsResponse> getRecentItems(String url) {

        Log.e(TAG, "RECENT ITEMS URL :: " + url);
        ArrayList<RecentItemsResponse> recent_items_response_list = new ArrayList<>();
        RecentItemsResponse recent_items_response;
        JSONObject jsonobject;

        StringBuilder response = new StringBuilder();
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-ESM-KEY", Constants.API_KEY);
            con.setRequestProperty("X-ESM-LID", AppController.APP_LANGUAGE);
            con.setRequestProperty("X-ESM-AT", Constants.AUTHORIZATION_TOKEN);

            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) { //success

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.e(TAG, "RECENT ITEMS RESPONSE :: " + response);
                JSONArray jsonArray = new JSONArray(response.toString());
                Log.e(TAG, "jsonArray size : " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    recent_items_response = new RecentItemsResponse();
                    jsonobject = jsonArray.getJSONObject(i);

                    recent_items_response.setBid_current(jsonobject.getInt("bid_current"));
                    recent_items_response.setBid_end(jsonobject.getString("bid_end"));
                    recent_items_response.setBid_exp(jsonobject.getDouble("bid_exp"));
                    recent_items_response.setBid_next(jsonobject.getInt("bid_next"));
                    recent_items_response.setStatus(jsonobject.getInt("status"));
                    recent_items_response.setDisc_amt(jsonobject.getDouble("disc_amt"));
                    recent_items_response.setBids(jsonobject.getInt("bids"));
                    recent_items_response.setFormat(jsonobject.getInt("format"));
                    recent_items_response.setType(jsonobject.getInt("type"));
                    recent_items_response.setRate_curid(jsonobject.getInt("rate_curid"));
                    recent_items_response.setSid(jsonobject.getInt("sid"));
                    recent_items_response.setDisc_valid(jsonobject.getInt("disc_valid"));
                    recent_items_response.setId(jsonobject.getDouble("id"));
                    recent_items_response.setPics(jsonobject.getString("pics"));
                    recent_items_response.setUnit_label(jsonobject.getString("unit_label"));
                    recent_items_response.setRate(jsonobject.getString("rate"));
                    recent_items_response.setName(jsonobject.getString("name"));
                    recent_items_response.setIs_saved(jsonobject.getInt("is_saved"));
                    recent_items_response.setDisc_pct(jsonobject.getDouble("disc_pct"));

                    recent_items_response_list.add(recent_items_response);
                }
                return recent_items_response_list;
            }
        } catch (ConnectException e) {
            Log.e(TAG, "CHECKPOINT: Connection Exception :: " + e);
        } catch (Exception e) {
            Log.e(TAG, "CHECKPOINT: General Exception :: " + e);
        }
        return null;
    }


    //this method is for getting recent searches
    public static ArrayList<ItemRecentSearches> getRecentSearches(String url) {
        Log.e(TAG, "RECENT SEARCHES URL :: " + url);
        ArrayList<ItemRecentSearches> recent_searches_response_list = new ArrayList<>();
        ItemRecentSearches recent_searches_response;
        JSONObject jsonobject;

        StringBuilder response = new StringBuilder();
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-ESM-KEY", Constants.API_KEY);
            con.setRequestProperty("X-ESM-LID", AppController.APP_LANGUAGE);
            con.setRequestProperty("X-ESM-AT", Constants.AUTHORIZATION_TOKEN);

            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) { //success

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.e(TAG, "RECENT ITEMS RESPONSE :: " + response);
                JSONArray jsonArray = new JSONArray(response.toString());
                Log.e(TAG, "jsonArray size : " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    recent_searches_response = new ItemRecentSearches();
                    jsonobject = jsonArray.getJSONObject(i);

                    recent_searches_response.setId(jsonobject.getInt("id"));
                    recent_searches_response.setName(jsonobject.getString("name"));
                    recent_searches_response.setDescrip(jsonobject.getString("descrip"));
                    recent_searches_response.setEmail(jsonobject.getInt("emails"));

                    recent_searches_response_list.add(recent_searches_response);
                }
                return recent_searches_response_list;
            }
        } catch (ConnectException e) {
            Log.e(TAG, "CHECKPOINT: Connection Exception :: " + e);
        } catch (Exception e) {
            Log.e(TAG, "CHECKPOINT: General Exception :: " + e);
        }
        return null;
    }
}