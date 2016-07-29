package com.martoff.esmart.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.martoff.esmart.R;
import com.martoff.esmart.api_calls.WebServices;
import com.martoff.esmart.pojo.RecentItemsResponse;
import com.martoff.esmart.volley.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by Safeer on 14-May-16.
 */
public class RecentItemsAdapter extends RecyclerView.Adapter<RecentItemsAdapter.RecentItemViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<RecentItemsResponse> recent_items_list;
    RecentItemsResponse single_recent_item;
    String TAG = "RecentItemsAdapter";
    int width_of_iv, height_of_iv;
    String image_url;

    ImageLoader image_loader;
    RequestQueue requestQueue;

    int SCREEN_WIDTH;//for changing number of items according to screen
    DisplayMetrics metrics;

    public RecentItemsAdapter(Context context, ArrayList<RecentItemsResponse> recent_items_list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.recent_items_list = recent_items_list;

        metrics = new DisplayMetrics();
        SCREEN_WIDTH = context.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public RecentItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recent_items, parent, false);
        requestQueue = VolleySingleton.getInstance().getRequestQueue();//for initializing volley request queue
        image_loader = VolleySingleton.getInstance().getImage_loader();
        return new RecentItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecentItemViewHolder holder, int position) {
        single_recent_item = recent_items_list.get(position);

        //HERE GET DIMENSIONS OF SCREEN AND SET THIS ACCORDINGLY
        width_of_iv = (int) context.getResources().getDimension(R.dimen.recent_image_width);
        height_of_iv = (int) context.getResources().getDimension(R.dimen.recent_image_height);
        image_url = WebServices.IMAGE_URL(width_of_iv, height_of_iv, single_recent_item.getPics());

//        Log.e(TAG, "IMAGE URL :: " + image_url);

        holder.iv_recent_item.setImageUrl(image_url, image_loader);

        holder.tv_recent_item_name.setText(single_recent_item.getName());
        holder.tv_item_price.setText(single_recent_item.getRate());
    }

    @Override
    public int getItemCount() {

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (SCREEN_WIDTH < 600) {
                return 4;
            } else
                return recent_items_list.size();
        } else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return recent_items_list.size();
        }
        return recent_items_list.size();
    }

    class RecentItemViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView iv_recent_item;
        TextView tv_recent_item_name, tv_item_price, tv_buy_item;

        public RecentItemViewHolder(View itemView) {
            super(itemView);

            iv_recent_item = (NetworkImageView) itemView.findViewById(R.id.iv_recent_item);
            tv_recent_item_name = (TextView) itemView.findViewById(R.id.tv_recent_item_name);
            tv_item_price = (TextView) itemView.findViewById(R.id.tv_item_price);
            tv_buy_item = (TextView) itemView.findViewById(R.id.tv_buy_item);
        }
    }
}