package com.martoff.esmart.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.martoff.esmart.R;
import com.martoff.esmart.extras.Constants;

public class ShowProductsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    final String KEYWORDS_STORED = "keywords_stored";
    String TAG = "ShowProductsActivity";
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView rv_products_listing;

    View nav_header_view;
    LinearLayout nav_ll_main;
    //short layout
    RelativeLayout nav_rl_keywords, nav_rl_price;
    TextView tv_keywords_stored, tv_keywords_header;
    String keywords_stored;
    RelativeLayout.LayoutParams keywords_layout_params;

    //detailed layout
    RelativeLayout nav_rl_keywords_details_layout, nav_rl_price_details_layout;
    Button btn_keywords_apply, btn_keywords_clear;
    AutoCompleteTextView et_keywords_to_store;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_products);
        setTitle("");

        int cat_id, menu_type, group_id, menu_id;
        cat_id = getIntent().getExtras().getInt(Constants.CAT_ID);
        menu_type = getIntent().getExtras().getInt(Constants.MENU_TYPE);
        group_id = getIntent().getExtras().getInt(Constants.GROUP_ID);
        menu_id = getIntent().getExtras().getInt(Constants.MENU_ID);

        Log.e(TAG, "CAT_ID :: " + cat_id);
        Log.e(TAG, "MENU_TYPE :: " + menu_type);
        Log.e(TAG, "GROUP_ID :: " + group_id);
        Log.e(TAG, "MENU_ID :: " + menu_id);

        main_initializations(); //main controls initializations
        nav_initializations(); //navigation controls initializations

        //TODO make api call
        getProductsListing(cat_id, menu_id, menu_type, group_id);
    }

    private void getProductsListing(int cat_id, int menu_id, int menu_type, int group_id) {

    }

    private void main_initializations() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar_show_products);
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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        rv_products_listing = (RecyclerView) findViewById(R.id.rv_products_listing);
        rv_products_listing.setLayoutManager(new LinearLayoutManager(this));


    }

    private void nav_initializations() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_header_view = navigationView.getHeaderView(0);

        nav_ll_main = (LinearLayout) nav_header_view.findViewById(R.id.nav_ll_main);

        //short layouts
        nav_rl_keywords = (RelativeLayout) nav_header_view.findViewById(R.id.nav_rl_keywords);
        tv_keywords_header = (TextView) nav_header_view.findViewById(R.id.tv_keywords_header);
        tv_keywords_stored = (TextView) nav_header_view.findViewById(R.id.tv_keywords_stored);

        keywords_layout_params = (RelativeLayout.LayoutParams) tv_keywords_header.getLayoutParams();

        keywords_stored = sharedPreferences.getString(KEYWORDS_STORED, "");
        if (keywords_stored.isEmpty()) {
            keywords_layout_params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            tv_keywords_header.setLayoutParams(keywords_layout_params);
        } else {
            tv_keywords_stored.setText(keywords_stored);
            keywords_layout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            tv_keywords_header.setLayoutParams(keywords_layout_params);
        }

        nav_rl_price = (RelativeLayout) nav_header_view.findViewById(R.id.nav_rl_price);

        //detailed layout
        nav_rl_keywords_details_layout = (RelativeLayout) nav_header_view.findViewById(R.id.nav_rl_keywords_details_layout);
        et_keywords_to_store = (AutoCompleteTextView) nav_header_view.findViewById(R.id.et_keywords_to_store);
        et_keywords_to_store.setText(sharedPreferences.getString(KEYWORDS_STORED, ""));

        btn_keywords_apply = (Button) nav_header_view.findViewById(R.id.btn_apply_keywords);
        btn_keywords_apply.setOnClickListener(this);

        btn_keywords_clear = (Button) nav_header_view.findViewById(R.id.btn_clear_keywords);
        btn_keywords_clear.setOnClickListener(this);

        nav_rl_price_details_layout = (RelativeLayout) nav_header_view.findViewById(R.id.nav_rl_price_details_layout);

        //for setting height of detailed layout dynamically
        Display display = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        int height = outMetrics.heightPixels;
        if (height < 800) {
            height -= 25;
        } else {
            height -= 50;
        }

        nav_rl_keywords_details_layout.setMinimumHeight(height);
        nav_rl_price_details_layout.setMinimumHeight(height);

        //applying click listeners on layouts
        nav_rl_keywords.setOnClickListener(this);
        nav_rl_price.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            if (nav_ll_main.getVisibility() == View.GONE) {
                if (nav_rl_keywords_details_layout.getVisibility() == View.VISIBLE) {
                    nav_rl_keywords_details_layout.setVisibility(View.GONE);
                } else if (nav_rl_price_details_layout.getVisibility() == View.VISIBLE) {
                    nav_rl_price_details_layout.setVisibility(View.GONE);
                }

                //putting it here, because its the ultimately required
                nav_ll_main.setVisibility(View.VISIBLE);

            } else {
                drawer.closeDrawer(GravityCompat.START);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_rl_keywords:
                nav_ll_main.setVisibility(View.GONE);
                nav_rl_keywords_details_layout.setVisibility(View.VISIBLE);
                break;

            case R.id.nav_rl_price:
                nav_ll_main.setVisibility(View.GONE);
                nav_rl_price_details_layout.setVisibility(View.VISIBLE);
                break;

            case R.id.btn_apply_keywords:
                if (et_keywords_to_store.getText().toString().isEmpty()) {
                    onBackPressed();
                } else {
                    editor = sharedPreferences.edit();
                    editor.putString(KEYWORDS_STORED, et_keywords_to_store.getText().toString());
                    editor.apply();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        keywords_layout_params.removeRule(RelativeLayout.CENTER_VERTICAL);
                    } else {
                        keywords_layout_params.addRule(RelativeLayout.CENTER_VERTICAL, 0);
                    }
                    tv_keywords_header.setLayoutParams(keywords_layout_params);
                    tv_keywords_stored.setText(sharedPreferences.getString(KEYWORDS_STORED, ""));

                    onBackPressed();
                }
                break;

            case R.id.btn_clear_keywords:
                et_keywords_to_store.setText("");

                Log.e(TAG, "et_keywords.getText() :: " + et_keywords_to_store);

                editor = sharedPreferences.edit();
                editor.putString(KEYWORDS_STORED, et_keywords_to_store.getText().toString());
                editor.apply();

                Log.e(TAG, "updated keywords stored :: " + sharedPreferences.getString(KEYWORDS_STORED, ""));

                //now here set the layout
                keywords_layout_params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                tv_keywords_header.setLayoutParams(keywords_layout_params);

                tv_keywords_stored.setText(sharedPreferences.getString(KEYWORDS_STORED, ""));

                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_products, menu);
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

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}