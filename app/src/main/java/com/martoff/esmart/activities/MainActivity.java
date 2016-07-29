package com.martoff.esmart.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.martoff.esmart.AppController;
import com.martoff.esmart.R;
import com.martoff.esmart.api_calls.WebServices;
import com.martoff.esmart.extras.Constants;
import com.martoff.esmart.fragments.GeneralFragment;
import com.martoff.esmart.fragments.HomeFragment;
import com.martoff.esmart.pojo.InitResponse;
import com.martoff.esmart.pojo.ResponseMods;
import com.martoff.esmart.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    TabLayout tabLayout;
    DrawerLayout drawer;
    NavigationView navigationView;
    String TAG = "MainActivity";
    ProgressDialog dialog;

    SharedPreferences preferenceManager;
    SharedPreferences.Editor editor;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = MainActivity.this;

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferenceManager.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        if (AppController.isNetworkAvailable()) {
            ApiCall();
        } else {
            showNetworkIssueDialog();
        }
    }

    private void showNetworkIssueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.check_network);
        builder.setPositiveButton(R.string.Retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (AppController.isNetworkAvailable()) {
                    ApiCall();
                } else {
                    showNetworkIssueDialog();
                }
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                MainActivity.this.finish();
            }
        });
        builder.show();
    }

    private void ApiCall() {
        if (AppController.isNetworkAvailable()) {

            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            dialog.setMessage(getString(R.string.loading_data));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    WebServices.INITIALIZING_CALL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "InitResponse ::: " + response);

//                    HAVE TO SAVE USER ID FOR FURTHER CALS
//                    editor.putInt(Constants.GUEST_USER_ID, initResponse.getUid());
//                    editor.apply();


                    //now parse this response and go accordingly
                    InitResponse initResponse = parseInitResponse(response);
                    //this {parseInitResponse(response);} call has parsed jsonObject,
                    //now get and set toolbar items and also create fragments
                    initialize(initResponse);

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VolleyError ::: " + error);
                    if (error.toString().equals("com.android.volley.TimeoutError")) {
                        Log.e(TAG, "");
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        ApiCall();
                    } else {
                        Toast.makeText(context, "Something went wrong, Please try again", Toast.LENGTH_LONG).show();
                        MainActivity.this.finish();
                    }
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map params = new HashMap();
                    params.put("device", Constants.SENDER_ID);
                    params.put("uid", "0");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map headers = new HashMap();
                    headers.put("X-ESM-LID", AppController.APP_LANGUAGE);
                    headers.put("X-ESM-KEY", Constants.API_KEY);
                    return headers;
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    //here parsing json and returning POJO to further set the toolbar items
    private InitResponse parseInitResponse(String response) {
        InitResponse server_Init_response = new InitResponse();
        try {
            JSONObject json_object = new JSONObject(response);
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
        } catch (JSONException e) {
            Log.e(TAG, "JSONException ::: " + e);
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void initialize(InitResponse initResponse) {
        toolbar = (Toolbar) findViewById(R.id.toolbar_show_products);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //HOME fragment will always be there. So this is static
        ArrayList<String> fragment_titles_list = new ArrayList<>();
        for (int i=0; i<initResponse.getResponse_mods().size(); i++){
            fragment_titles_list.add(initResponse.getResponse_mods().get(i).getLabel());
        }
        fragment_titles_list.add(0, getResources().getString(R.string.all_of_esmart));
        mSectionsPagerAdapter.addFragment(HomeFragment.createInstance(fragment_titles_list), "Home");

        //now here passing the all information to newly created fragment,
        //therefore passing the complete object of ResponseMods class
        //initResponse.getResponse_mods().size() this will tell me NUMBER OF FRAGMENTS
        ResponseMods responseMods;
        for (int i = 0; i < initResponse.getResponse_mods().size(); i++) {
            responseMods = initResponse.getResponse_mods().get(i);
            mSectionsPagerAdapter.addFragment(GeneralFragment.createInstance(responseMods), responseMods.getLabel());
        }

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOffscreenPageLimit(6); //for keeping fragments in memory

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
        }
        else if (id == R.id.nav_cart) {

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_messages) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();//this is local list

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}