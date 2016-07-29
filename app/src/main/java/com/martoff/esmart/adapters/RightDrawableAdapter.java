package com.martoff.esmart.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martoff.esmart.R;

import java.util.ArrayList;

/**
 * Created by Safeer on 25-May-16.
 */
public class RightDrawableAdapter extends RecyclerView.Adapter<RightDrawableAdapter.RightDrawableHolder> {

    Context context;
    ArrayList<String> titles_list_items;
    LayoutInflater inflater;
    RightDrawableClickListener clickListener;
    String title;

    public RightDrawableAdapter(ArrayList<String> titles_list_items, Context context, RightDrawableClickListener clickListener) {
        this.context = context;
        this.titles_list_items = titles_list_items;
        this.clickListener = clickListener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RightDrawableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_right_drawable, parent, false);
        return new RightDrawableHolder(view);
    }

    @Override
    public void onBindViewHolder(RightDrawableHolder holder, int position) {
        title = titles_list_items.get(position);
        holder.tv_titles_list.setText(title);
    }

    @Override
    public int getItemCount() {
        return titles_list_items.size();
    }

    class RightDrawableHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_titles_list;

        public RightDrawableHolder(View itemView) {
            super(itemView);
            tv_titles_list = (TextView) itemView.findViewById(R.id.tv_right_drawable);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.categoryClickListener(v, getAdapterPosition());
        }
    }

    //to handle its clicks in HomeFragment
    public interface RightDrawableClickListener {
        void categoryClickListener(View v, int position);
    }
}