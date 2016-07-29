package com.martoff.esmart.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.martoff.esmart.AppController;
import com.martoff.esmart.R;
import com.martoff.esmart.activities.SearchActivity;
import com.martoff.esmart.adapters.RecentItemsAdapter;
import com.martoff.esmart.adapters.RecentSearchesAdapter;
import com.martoff.esmart.adapters.RightDrawableAdapter;
import com.martoff.esmart.api_calls.WebServices;
import com.martoff.esmart.extras.Constants;
import com.martoff.esmart.pojo.ItemRecentSearches;
import com.martoff.esmart.pojo.RecentItemsResponse;
import com.martoff.esmart.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnTouchListener, RightDrawableAdapter.RightDrawableClickListener, View.OnClickListener {

    private final String FRAGMENT_TITLES_LIST = "fragment_titles_list";
    private final String CATEGORY_SELECTED = "category_selected";
    String TAG = "HomeFragment";
    EditText et_search_launcher;
    RecyclerView rv_recent_items, rv_recent_searches;//rv_right_drawable_items,
    GridLayoutManager grid_layout_manager;
    Context context;
    ArrayList<String> fragment_titles_list;
    ScrollView home_layout_sv;//this is for making the lists disappear when somewhere else is clicked
    int SCREEN_WIDTH;//for changing number of items according to screen
    DisplayMetrics metrics;
    //these are for recent items response
    RecentItemsResponse recent_items_response;
    JSONObject jsonobject;
    ProgressDialog dialog;
    int menu_id_to_search = 0;//this is the menu id for searching api

    public HomeFragment() {
    }

    //i did this to get list of all fragment titles, which will be shown in rv_right_drawable
    public static HomeFragment createInstance(ArrayList<String> fragment_titles_list) {
        HomeFragment homeFragment = new HomeFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("fragment_titles_list", fragment_titles_list);
        homeFragment.setArguments(bundle);

        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragment_titles_list = getArguments().getStringArrayList("fragment_titles_list");

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        int user_id = 2;

        //to avoid dialog issue
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setMessage(getResources().getString(R.string.loading_data));
        //to avoid dialog issue

        getRecentItems(user_id); //volley call for getting recent items
        getRecentSearches(user_id);

        init(view);
        return view;
    }

    private void getRecentSearches(final int user_id) {
        final ArrayList<ItemRecentSearches> recent_searches_response_list = new ArrayList<>();
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

        dialog.show();

//        WebRequests.getRecentSearches(WebServices.RECENT_SEARCHES_URL(params[0]));
        JsonArrayRequest recentSearchesRequest = new JsonArrayRequest(Request.Method.GET,
                WebServices.RECENT_SEARCHES_URL(user_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.e(TAG, "RecentSearches Response :: " + response);

                        ItemRecentSearches recent_searches_response;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                recent_searches_response = new ItemRecentSearches();
                                jsonobject = response.getJSONObject(i);

                                recent_searches_response.setId(jsonobject.getInt("id"));
                                recent_searches_response.setName(jsonobject.getString("name"));
                                recent_searches_response.setDescrip(jsonobject.getString("descrip"));
                                recent_searches_response.setEmail(jsonobject.getInt("emails"));

                                recent_searches_response_list.add(recent_searches_response);
                            }

                            rv_recent_searches.setAdapter(new RecentSearchesAdapter(context, recent_searches_response_list));

                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "Recent Searches JSONException :: " + e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Recent Searches Volley Error :: " + error);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Log.e(TAG, "");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    getRecentSearches(user_id);
                }
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put(Constants.HEADER_X_ESM_KEY, Constants.API_KEY);
                headers.put(Constants.HEADER_X_ESM_LID, AppController.APP_LANGUAGE);
                headers.put(Constants.HEADER_X_ESM_AT, Constants.AUTHORIZATION_TOKEN);
                return headers;
            }
        };

        requestQueue.add(recentSearchesRequest);
    }

    private void getRecentItems(int user_id) {
        final ArrayList<RecentItemsResponse> recent_items_response_list = new ArrayList<>();
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

        dialog.show();
        JsonArrayRequest recentItemsRequest = new JsonArrayRequest(Request.Method.GET,
                WebServices.RECENT_ITEMS_URL(user_id),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArrayResponse) {
                        Log.e(TAG, "Recent items response :: " + jsonArrayResponse);
                        try {
                            for (int i = 0; i < jsonArrayResponse.length(); i++) {
                                recent_items_response = new RecentItemsResponse();
                                jsonobject = jsonArrayResponse.getJSONObject(i);

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
                            rv_recent_items.setAdapter(new RecentItemsAdapter(context, recent_items_response_list));
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
                Log.e(TAG, "Recent items error:: " + error);
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

    @Override
    public void categoryClickListener(View v, int position) {
        et_search_launcher.setHint(fragment_titles_list.get(position));
        menu_id_to_search = -position;
        Log.e(TAG, "menu_id_to_search : " + menu_id_to_search);

//        if (rv_right_drawable_items.getVisibility() == View.VISIBLE)
//            rv_right_drawable_items.setVisibility(View.GONE);
    }

    public void init(View view) {
        home_layout_sv = (ScrollView) view.findViewById(R.id.home_layout_sv);
        home_layout_sv.setOnTouchListener(this);

        et_search_launcher = (EditText) view.findViewById(R.id.et_search_launcher);
        et_search_launcher.setOnTouchListener(this);
        et_search_launcher.setOnClickListener(this);
//        et_search_launcher.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                Intent search_activity_intent = new Intent(context, SearchActivity.class);
////                search_activity_intent.putStringArrayListExtra(FRAGMENT_TITLES_LIST, fragment_titles_list);
////                search_activity_intent.putExtra(CATEGORY_SELECTED, menu_id_to_search);
////                startActivity(search_activity_intent);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });


        //categories recycler view
//        rv_right_drawable_items = (RecyclerView) view.findViewById(R.id.rv_right_drawable_items);
//        rv_right_drawable_items.setAdapter(new RightDrawableAdapter(fragment_titles_list, context, this));
//        rv_right_drawable_items.setLayoutManager(new LinearLayoutManager(context));


        //RECENT ITEMS RECYCLER VIEW
        rv_recent_items = (RecyclerView) view.findViewById(R.id.rv_recent_items);
        rv_recent_items.setHasFixedSize(true);

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //getting width of screen
        SCREEN_WIDTH = metrics.widthPixels;
        Log.e(TAG, "SCREEN_WIDTH : " + SCREEN_WIDTH);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //2 is the number of rows
            //in case of HORIZONTAL 2 is number of rows
            grid_layout_manager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
            rv_recent_items.setLayoutManager(grid_layout_manager);

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            if (SCREEN_WIDTH >= 600 && SCREEN_WIDTH <= 800) {
            if (SCREEN_WIDTH <= 800) {
                //2 is the number of rows
                grid_layout_manager = new GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false);
                rv_recent_items.setLayoutManager(grid_layout_manager);
            } else if (SCREEN_WIDTH > 800) {
                grid_layout_manager = new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);
                rv_recent_items.setLayoutManager(grid_layout_manager);
            }
        }


        //RECYCLER VIEW OF RECENT SEARCHES
        rv_recent_searches = (RecyclerView) view.findViewById(R.id.rv_recent_searches);
        rv_recent_searches.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        //setting adapter in AsyncTask
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
//            case R.id.et_search_launcher:
//                final int DRAWABLE_RIGHT = 2;
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (event.getRawX() >= (et_search_launcher.getRight() - et_search_launcher.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
////                        if (rv_right_drawable_items.getVisibility() == View.GONE)
////                            rv_right_drawable_items.setVisibility(View.VISIBLE);
////                        else
////                            rv_right_drawable_items.setVisibility(View.GONE);
//                        return true;
//                    } else {
//                        et_search_launcher.performClick();//for opening SearchActivity
//                    }
//                }
//                break;
            case R.id.home_layout_sv:
//                if (rv_right_drawable_items.getVisibility() == View.VISIBLE)
//                    rv_right_drawable_items.setVisibility(View.GONE);
                break;
        }
        return false;
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

    @Override
    public void onPause() {
        super.onPause();

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_search_launcher:
                Intent search_activity_intent = new Intent(context, SearchActivity.class);
                search_activity_intent.putStringArrayListExtra(FRAGMENT_TITLES_LIST, fragment_titles_list);
                search_activity_intent.putExtra(CATEGORY_SELECTED, menu_id_to_search);
                startActivity(search_activity_intent);
                break;
        }
    }
}