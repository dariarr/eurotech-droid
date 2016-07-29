package com.martoff.esmart.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martoff.esmart.R;
import com.martoff.esmart.pojo.CategoriesResponse;

import java.util.ArrayList;

/**
 * Created by Safeer on 01-Jun-16.
 */
public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.RecyclerItemHolder> {

    ArrayList<CategoriesResponse> category_response_list;
    Context context;
    LayoutInflater inflater;
    RecyclerItemClickListener click_listener; //this is listener and clicks will be handled by fragment instead adapter
    String TAG = "CategoriesRecyclerAdapter";
    String fragment_title;

    public CategoriesRecyclerAdapter(Context context, ArrayList<CategoriesResponse> category_response_list,
                                     RecyclerItemClickListener click_listener, String fragment_title) {
        this.fragment_title = fragment_title;//this is just for saving fragment title, which may be needed for future calls
        this.category_response_list = category_response_list;
        this.context = context;
        this.click_listener = click_listener;
        inflater = LayoutInflater.from(context);
    }

    public CategoriesRecyclerAdapter(Context context, ArrayList<CategoriesResponse> category_response_list,
                                     RecyclerItemClickListener click_listener) {
        this.category_response_list = category_response_list;
        this.context = context;
        this.click_listener = click_listener;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_category, parent, false);
        return new RecyclerItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerItemHolder holder, int position) {
        holder.tv_cat_name.setText(category_response_list.get(position).getLabel());
        holder.tv_cat_count.setText("(" + category_response_list.get(position).getCount() + ")");
        if (category_response_list.get(position).getCat_id() == 0)
            holder.iv_cat_indicator.setImageResource(R.drawable.expand_list);
        else
            holder.iv_cat_indicator.setImageResource(R.drawable.arrow);
    }

    @Override
    public int getItemCount() {
        return category_response_list.size();
    }

    public interface RecyclerItemClickListener {
        //cat_id is for checking that is there more listing or direct items to show
        void itemClickListener(int menu_id, int group_id, int cat_id);
    }

    class RecyclerItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int menu_id, group_id, cat_id;//cat_id is for checking that is there more listing or direct items to show
        TextView tv_cat_name, tv_cat_count;
        ImageView iv_cat_indicator;

        public RecyclerItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tv_cat_name = (TextView) itemView.findViewById(R.id.tv_cat_name);
            tv_cat_count = (TextView) itemView.findViewById(R.id.tv_cat_count);
            iv_cat_indicator = (ImageView) itemView.findViewById(R.id.iv_cat_indicator);
        }

        @Override
        public void onClick(View v) {
            cat_id = category_response_list.get(getAdapterPosition()).getCat_id();
            menu_id = category_response_list.get(getAdapterPosition()).getMenu_id();
            group_id = category_response_list.get(getAdapterPosition()).getGroup_id();

            // type + "/" + menu_id + "/" + group_id;
            Log.e(TAG, "menu_id = " + menu_id + ", group_id = " + group_id + ", cat_id = " + cat_id);

            click_listener.itemClickListener(menu_id, group_id, cat_id);
        }
    }
}