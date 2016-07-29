package com.martoff.esmart.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.martoff.esmart.AppController;
import com.martoff.esmart.R;
import com.martoff.esmart.adapters.RightDrawableAdapter;
import com.martoff.esmart.adapters.SearchResultAdapter;
import com.martoff.esmart.api_calls.WebServices;
import com.martoff.esmart.extras.Constants;
import com.martoff.esmart.pojo.SearchIntellisense;
import com.martoff.esmart.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements View.OnTouchListener, RightDrawableAdapter.RightDrawableClickListener, SearchResultAdapter.SearchResultItemClickListener {

    RecyclerView rv_search_results, rv_right_drawable_items;
    EditText et_search;
    ArrayList<String> fragment_titles_list;
    ArrayList<SearchIntellisense> searchIntellisenseList;
    SearchResultAdapter adapter;

    private final String FRAGMENT_TITLES_LIST = "fragment_titles_list";
    private final String CATEGORY_SELECTED = "category_selected";

    String TAG = "SearchActivity";
    int menu_id_to_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("");

        initialize();
    }

    private void initialize() {

        fragment_titles_list = getIntent().getStringArrayListExtra(FRAGMENT_TITLES_LIST);
        menu_id_to_search = getIntent().getIntExtra(CATEGORY_SELECTED, 0);

        searchIntellisenseList = new ArrayList<>();

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setHint(fragment_titles_list.get(-(menu_id_to_search)));//this is because from intent menu_id_to_search can be -1,-2 and so on
        et_search.setOnTouchListener(this);
        et_search.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 1000; // milliseconds

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {

                if(s.length()>0){
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    Log.e(TAG, "Make api call from here");
                                    if (AppController.isNetworkAvailable()) {
                                        volleySearchRequest(s.toString());
                                    } else {
                                        showNetworkIssueDialog();
                                    }
                                }
                            },
                            DELAY
                    );
                }
                else {
                    //clear the list here and notify dataset changed.
                    Log.e(TAG, "List is cleared due to no search text");
                    searchIntellisenseList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        rv_right_drawable_items = (RecyclerView) findViewById(R.id.rv_right_drawable_items);
        rv_right_drawable_items.setAdapter(new RightDrawableAdapter(fragment_titles_list, this, this));
        rv_right_drawable_items.setLayoutManager(new LinearLayoutManager(this));

        //search items recycler view
        rv_search_results = (RecyclerView) findViewById(R.id.rv_search_results);
        rv_search_results.setLayoutManager(new LinearLayoutManager(this));
    }


    //menu id to search is already updated from recycler click, so no need to pass here
    private void volleySearchRequest(String to_search) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

        Log.e(TAG, "SEARCH WORD LENGTH :: " + to_search.length());
        Log.e(TAG, "SEARCH URL :: " + WebServices.SEARCH_URL(to_search, 2));

        Log.e(TAG, "TO_SEARCH :: " + to_search);
        Log.e(TAG, "MENU ID TO SEARCH :: " + menu_id_to_search);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                WebServices.SEARCH_URL(to_search, menu_id_to_search),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, "SEARCH RESPONSE :: " + response);

                        if (searchIntellisenseList.isEmpty()) {
                            Log.e(TAG, "List is empty");
                        } else {
                            Log.e(TAG, "List is containing items");
                            searchIntellisenseList.clear();
                            adapter.notifyDataSetChanged();
                        }

                        SearchIntellisense searchIntellisense;
                        try {
                            JSONArray jsonArray = response.getJSONArray(0);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                searchIntellisense = new SearchIntellisense();
                                JSONObject jsonobject = jsonArray.getJSONObject(i);

                                searchIntellisense.setId(jsonobject.getInt("id"));
                                searchIntellisense.setMenu(jsonobject.getString("menu"));
                                searchIntellisense.setName(jsonobject.getString("name"));

                                searchIntellisenseList.add(searchIntellisense);
                            }

                            //this is for removing duplicate items from arraylist starts
//                                firstly confirm from misha, and them implement this code
//                                Set<String> hs = new HashSet<>();
//                                hs.addAll(search_results_list);
//                                search_results_list.clear();
//                                search_results_list.addAll(hs);
                            //this is for removing duplicate items from arraylist ends


                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException while parsing categories :: " + e);
                        }

                        try {
                            JSONArray jsonArray = response.getJSONArray(1);
                            Log.e(TAG, "jsonObject of searches :: " + jsonArray);
                            for (int i = 0; i < jsonArray.length(); i++) {

                                searchIntellisense = new SearchIntellisense();
                                JSONObject jsonobject = jsonArray.getJSONObject(i);

                                searchIntellisense.setId(jsonobject.getInt("id"));
                                searchIntellisense.setType(jsonobject.getInt("type"));
                                searchIntellisense.setName(jsonobject.getString("name"));
                                searchIntellisense.setRate(jsonobject.getDouble("rate"));
                                searchIntellisense.setRate_curid(jsonobject.getInt("rate_curid"));
                                searchIntellisense.setPrice_from(jsonobject.getString("price_from"));
                                searchIntellisense.setUnit_label(jsonobject.getString("unit_label"));
                                searchIntellisense.setMark(jsonobject.getString("mark"));
                                searchIntellisense.setFilename(jsonobject.getString("filename"));

                                searchIntellisenseList.add(searchIntellisense);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException :: " + e);
                        }

                        adapter = new SearchResultAdapter(searchIntellisenseList, SearchActivity.this, SearchActivity.this);//last param is for clicklistener
                        rv_search_results.setAdapter(adapter);

                        if (rv_search_results.getVisibility() == View.GONE) {
                            rv_search_results.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ERROR :: " + error);
                if (error.toString().equals("com.android.volley.TimeoutError")) {
                    Log.e(TAG, "Due to timeout, making call again.");
                    volleySearchRequest(et_search.getText().toString());
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

    private void showNetworkIssueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.check_network);
        builder.setPositiveButton(R.string.Retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (AppController.isNetworkAvailable()) {
                    volleySearchRequest(et_search.getText().toString());
                } else {
                    showNetworkIssueDialog();
                }
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                SearchActivity.this.finish(); //finish the activity
            }
        });
        builder.show();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.et_search:

                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_LEFT = 0;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //for making right drawable clickable
                    if (event.getRawX() >= (et_search.getRight() - et_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (rv_right_drawable_items.getVisibility() == View.GONE) {
                            if (rv_search_results.getVisibility() == View.VISIBLE) {
                                rv_search_results.setVisibility(View.GONE);
                            }
                            rv_right_drawable_items.setVisibility(View.VISIBLE);
                        } else
                            rv_right_drawable_items.setVisibility(View.GONE);
                        return true;
                    } else {
                        if (rv_right_drawable_items.getVisibility() == View.VISIBLE)
                            rv_right_drawable_items.setVisibility(View.GONE);
                    }

                    //for making back arrow clickable
                    if (event.getX() <= (et_search.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        SearchActivity.this.finish();
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void categoryClickListener(View v, int position) {
        et_search.setHint(fragment_titles_list.get(position));
        menu_id_to_search = -position;
        Log.e(TAG, "menu_id_to_search : " + menu_id_to_search);

        if (rv_right_drawable_items.getVisibility() == View.VISIBLE)
            rv_right_drawable_items.setVisibility(View.GONE);
    }

    @Override
    public void searchItemClickListener(View view, int position) {
        Log.e(TAG, "Tapped position : " + position);

        if (searchIntellisenseList.get(position).getFilename() != null) {
            et_search.setText(searchIntellisenseList.get(position).getName());
        } else {
            et_search.setText(searchIntellisenseList.get(position).getMenu());
        }

        if (rv_search_results.getVisibility() == View.VISIBLE)
            rv_search_results.setVisibility(View.GONE);
    }
}