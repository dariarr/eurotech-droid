package com.martoff.esmart.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.martoff.esmart.AppController;
import com.martoff.esmart.R;
import com.martoff.esmart.activities.CategoriesListingActivity;
import com.martoff.esmart.activities.ShowProductsActivity;
import com.martoff.esmart.adapters.CategoriesRecyclerAdapter;
import com.martoff.esmart.adapters.RecentItemsMotorAdapter;
import com.martoff.esmart.api_calls.WebServices;
import com.martoff.esmart.extras.Constants;
import com.martoff.esmart.pojo.CategoriesResponse;
import com.martoff.esmart.pojo.RecentItemsResponse;
import com.martoff.esmart.pojo.ResponseMods;
import com.martoff.esmart.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Safeer on 23-May-16.
 */
public class GeneralFragment extends Fragment implements CategoriesRecyclerAdapter.RecyclerItemClickListener {

    //{"type":8,"menu_id":0,"group_id":0,"cat_id":0,"label":"Products","count":0},
    private final static String MENU_TYPE = "menu_type";
    private final static String MENU_ID = "menu_id"; //
    private final static String GROUP_ID = "group_id";
    private final static String MENU_LABEL = "menu_label";
    public static String TAG = "GeneralFragment";
    Context context;
    //    TextView tv_tree;//for showing category tree on top
    RecyclerView rv_categories_items, rv_recent_items_motors;

    //THIS MAY BE NEEDED TO CHANGE, BECAUSE IN THIS A FAVORITE MARK IS THERE
    RecentItemsResponse recent_items_response;
    JSONObject jsonObject;
    ProgressDialog dialog;

    public GeneralFragment() {
    }

    public static GeneralFragment createInstance(ResponseMods responseMods) {
        GeneralFragment generalFragment = new GeneralFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(MENU_TYPE, responseMods.getType());
        bundle.putInt(MENU_ID, responseMods.getMenu_id());
        bundle.putInt(GROUP_ID, responseMods.getGroup_id());
        bundle.putString(MENU_LABEL, responseMods.getLabel());

        generalFragment.setArguments(bundle);
        return generalFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        if (getArguments().getInt(MENU_TYPE) == 8 || getArguments().getInt(MENU_TYPE) == 9) {
            rootView = inflater.inflate(R.layout.fragment_general, container, false);
            initCategories(rootView);
        } else if (getArguments().getInt(MENU_TYPE) == 1) {
            rootView = inflater.inflate(R.layout.fragment_motors, container, false);
            initMotors(rootView);
        } else {
            //this is just to avoid, if type is not from those listed here
            rootView = inflater.inflate(R.layout.fragment_type_not_available, container, false);
        }
        loadCategoryData();
        return rootView;
    }

    private void initMotors(View view) {
        rv_recent_items_motors = (RecyclerView) view.findViewById(R.id.rv_recent_items_motors);
        rv_recent_items_motors.setHasFixedSize(true);

        GridLayoutManager grid_layout_manager;
        int SCREEN_WIDTH;//for changing number of items according to screen
        DisplayMetrics metrics;

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //getting width of screen
        SCREEN_WIDTH = metrics.widthPixels;
        Log.e(TAG, "SCREEN_WIDTH : " + SCREEN_WIDTH);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //2 is the number of rows
            //in case of HORIZONTAL 2 is number of rows
            grid_layout_manager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
            rv_recent_items_motors.setLayoutManager(grid_layout_manager);

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.e(TAG, "ORIENTATION IS LANDSCAPE WITH WIDTH :::: " + SCREEN_WIDTH);
            if (SCREEN_WIDTH <= 800) {
                //2 is the number of rows
                grid_layout_manager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
                rv_recent_items_motors.setLayoutManager(grid_layout_manager);
            } else if (SCREEN_WIDTH > 800) {
                grid_layout_manager = new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);
                rv_recent_items_motors.setLayoutManager(grid_layout_manager);
            }
        }

        loadRecentItemsMotors();
    }

    //loading recent items in motors fragment
    private void loadRecentItemsMotors() {

        final ArrayList<RecentItemsResponse> recent_items_response_list_motors = new ArrayList<>();
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setMessage(getResources().getString(R.string.loading_data));
        dialog.show();

        int user_id = 2;

        JsonArrayRequest recentItemsRequest = new JsonArrayRequest(Request.Method.GET,
                WebServices.RECENT_ITEMS_URL(user_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArrayResponse) {
                        Log.e(TAG, "MOTORS Recent items response :: " + jsonArrayResponse);
                        try {
                            for (int i = 0; i < jsonArrayResponse.length(); i++) {
                                recent_items_response = new RecentItemsResponse();
                                jsonObject = jsonArrayResponse.getJSONObject(i);

                                recent_items_response.setBid_current(jsonObject.getInt("bid_current"));
                                recent_items_response.setBid_end(jsonObject.getString("bid_end"));
                                recent_items_response.setBid_exp(jsonObject.getDouble("bid_exp"));
                                recent_items_response.setBid_next(jsonObject.getInt("bid_next"));
                                recent_items_response.setStatus(jsonObject.getInt("status"));
                                recent_items_response.setDisc_amt(jsonObject.getDouble("disc_amt"));
                                recent_items_response.setBids(jsonObject.getInt("bids"));
                                recent_items_response.setFormat(jsonObject.getInt("format"));
                                recent_items_response.setType(jsonObject.getInt("type"));
                                recent_items_response.setRate_curid(jsonObject.getInt("rate_curid"));
                                recent_items_response.setSid(jsonObject.getInt("sid"));
                                recent_items_response.setDisc_valid(jsonObject.getInt("disc_valid"));
                                recent_items_response.setId(jsonObject.getDouble("id"));
                                recent_items_response.setPics(jsonObject.getString("pics"));
                                recent_items_response.setUnit_label(jsonObject.getString("unit_label"));
                                recent_items_response.setRate(jsonObject.getString("rate"));
                                recent_items_response.setName(jsonObject.getString("name"));
                                recent_items_response.setIs_saved(jsonObject.getInt("is_saved"));
                                recent_items_response.setDisc_pct(jsonObject.getDouble("disc_pct"));

                                recent_items_response_list_motors.add(recent_items_response);
                            }

                            rv_recent_items_motors.setAdapter(new RecentItemsMotorAdapter(context, recent_items_response_list_motors));
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
                Log.e(TAG, "MOTORS Recent items error:: " + error);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("X-ESM-KEY", Constants.API_KEY);
                headers.put("X-ESM-LID", AppController.APP_LANGUAGE);
                headers.put("X-ESM-AT", Constants.AUTHORIZATION_TOKEN);
                return headers;
            }
        };
        requestQueue.add(recentItemsRequest);
    }

    private void initCategories(View view) {
//        tv_tree = (TextView) view.findViewById(R.id.tv_tree);
//        tv_tree.setText(getArguments().getString(MENU_LABEL));

        rv_categories_items = (RecyclerView) view.findViewById(R.id.rv_category_items);
        rv_categories_items.setLayoutManager(new LinearLayoutManager(context));
        //adapter is set after network call
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadCategoryData() {

        //because categories are available only for MENU_TYPE 8 & 9
        if (getArguments().getInt(MENU_TYPE) != 1) {
            JSONObject jsonObjectServices = null;
            try {
                jsonObjectServices = new JSONObject();
                jsonObjectServices.accumulate(Constants.MENU_TYPE, getArguments().getInt(MENU_TYPE));
                jsonObjectServices.accumulate(Constants.MENU_ID, getArguments().getInt(MENU_ID));
                jsonObjectServices.accumulate(Constants.GROUP_ID, getArguments().getInt(GROUP_ID));
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :: " + e);
            }

            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

            String url = WebServices.CATEGORIES_RESPONSE(jsonObjectServices);
            Log.e(TAG, "REQUESTED URL : " + url);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                    url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray json_array_response) {
                    try {
//                    JSONArray json_array_response = new JSONArray(response.toString());
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
                        //4th param: this is the title of the fragment, which is need to detect click
                        rv_categories_items.setAdapter(new CategoriesRecyclerAdapter(context, category_response_list,
                                GeneralFragment.this, (getArguments().getString(MENU_LABEL))));
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException :: " + e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Volley Error :: " + error);
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
    }

    @Override
    public void itemClickListener(int menu_id, int group_id, int cat_id) {
        //cat_id IS FOR CHECKING THAT EITHER THERE EXISTS MORE LISTING or do I have to show 2.1.jpg
        // type + "/" + menu_id + "/" + group_id;

        if (cat_id > 0) {
            Intent intent = new Intent(getActivity(), ShowProductsActivity.class);
            intent.putExtra(Constants.MENU_TYPE, getArguments().getInt(MENU_TYPE));
            intent.putExtra(Constants.MENU_ID, menu_id);
            intent.putExtra(Constants.GROUP_ID, group_id);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), CategoriesListingActivity.class);
            intent.putExtra(Constants.MENU_TYPE, getArguments().getInt(MENU_TYPE));
            intent.putExtra(Constants.MENU_ID, menu_id);
            intent.putExtra(Constants.GROUP_ID, group_id);
            startActivity(intent);
        }
    }
}