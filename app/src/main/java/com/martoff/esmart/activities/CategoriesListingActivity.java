package com.martoff.esmart.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.martoff.esmart.AppController;
import com.martoff.esmart.R;
import com.martoff.esmart.adapters.CategoriesRecyclerAdapter;
import com.martoff.esmart.api_calls.WebServices;
import com.martoff.esmart.extras.Constants;
import com.martoff.esmart.pojo.CategoriesResponse;
import com.martoff.esmart.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriesListingActivity extends AppCompatActivity implements CategoriesRecyclerAdapter.RecyclerItemClickListener {

    String TAG = "CategoriesListingActivity";
    RecyclerView rv_categories_listing;
    int menu_type, menu_id, group_id;
    Toolbar toolbar;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_listing);
        setTitle("");

        toolbar = (Toolbar) findViewById(R.id.toolbar_categories_listing);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null)
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rv_categories_listing = (RecyclerView) findViewById(R.id.rv_category_listings);
        rv_categories_listing.setLayoutManager(new LinearLayoutManager(this));

        menu_type = getIntent().getIntExtra(Constants.MENU_TYPE, 0);
        menu_id = getIntent().getIntExtra(Constants.MENU_ID, 0);
        group_id = getIntent().getIntExtra(Constants.GROUP_ID, 0);

        fetchItemData(menu_type, menu_id, group_id);
    }


    private void fetchItemData(int type, final int menu_id, final int group_id) {

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setMessage(getResources().getString(R.string.loading_data));
        dialog.show();

        JSONObject jsonObjectServices = null;
        try {
            jsonObjectServices = new JSONObject();
            jsonObjectServices.accumulate(Constants.MENU_TYPE, type);
            jsonObjectServices.accumulate(Constants.MENU_ID, menu_id);
            jsonObjectServices.accumulate(Constants.GROUP_ID, group_id);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException :: " + e);
        }

        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

        String url = WebServices.CATEGORIES_RESPONSE(jsonObjectServices);
        Log.e(TAG, "ITEM REQUESTED URL : " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray json_array_response) {
                try {
                    ArrayList<CategoriesResponse> category_response_list = new ArrayList<>();
                    CategoriesResponse category_response;

                    //response single item ::[{"menu_id":1,"group_id":0,"cat_id":0,"label":"Automotive","count":56}]
                    Log.e(TAG, "json_array_response :: " + json_array_response);
                    Log.e(TAG, "json_array_response :: " + json_array_response.length());
                    for (int i = 0; i < json_array_response.length(); i++) {

                        JSONObject jsonObject = json_array_response.getJSONObject(i);
                        category_response = new CategoriesResponse();

                        category_response.setMenu_id(jsonObject.getInt(Constants.MENU_ID));
                        category_response.setGroup_id(jsonObject.getInt(Constants.GROUP_ID));
                        category_response.setCat_id(jsonObject.getInt(Constants.CAT_ID));
                        category_response.setLabel(jsonObject.getString(Constants.LABEL));
                        category_response.setCount(jsonObject.getInt(Constants.COUNT));

                        category_response_list.add(category_response);
                    }

                    //1st param: context
                    //2nd param: list to populate in recycler view
                    //3rd param: fragment, this is required for implementation of click listener interface
                    rv_categories_listing.setAdapter(new CategoriesRecyclerAdapter(CategoriesListingActivity.this,
                            category_response_list,
                            CategoriesListingActivity.this));
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException :: " + e);
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error :: " + error);
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Log.e(TAG, "Due to timeout, making call again.");
                    fetchItemData(menu_type, menu_id, group_id);
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("X-ESM-LID", AppController.APP_LANGUAGE);
                headers.put("X-ESM-KEY", Constants.API_KEY);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void itemClickListener(int menu_id, int group_id, int cat_id) {
        Log.e(TAG, "menu_id = " + menu_id + ", group_id = " + group_id + ", cat_id = " + cat_id);

        if (cat_id > 0) {
            Intent intent = new Intent(this, ShowProductsActivity.class);
            intent.putExtra(Constants.MENU_TYPE, getIntent().getIntExtra(Constants.MENU_TYPE, 0));
            intent.putExtra(Constants.CAT_ID, cat_id);
            intent.putExtra(Constants.MENU_ID, menu_id);
            intent.putExtra(Constants.GROUP_ID, group_id);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, CategoriesListingActivity.class);
            intent.putExtra(Constants.MENU_TYPE, getIntent().getIntExtra(Constants.MENU_TYPE, 0));
            intent.putExtra(Constants.MENU_ID, menu_id);
            intent.putExtra(Constants.GROUP_ID, group_id);
            startActivity(intent);
        }
    }
}