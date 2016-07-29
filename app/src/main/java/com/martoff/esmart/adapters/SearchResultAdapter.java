package com.martoff.esmart.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martoff.esmart.R;
import com.martoff.esmart.pojo.SearchIntellisense;

import java.util.ArrayList;

/**
 * Created by Safeer on 26-May-16.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultItemHolder> {

    String TAG = "SearchResultAdapter";
    LayoutInflater inflater;
    Context context;
    //    ArrayList<String> search_results_list;
    ArrayList<SearchIntellisense> search_results_list;
    //    String search_result;
    SearchIntellisense search_result;
    SearchResultItemClickListener clickListener;

    //    public SearchResultAdapter(ArrayList<String> search_results_list, Context context, SearchResultItemClickListener clickListener) {
    public SearchResultAdapter(ArrayList<SearchIntellisense> search_results_list, Context context, SearchResultItemClickListener clickListener) {
        this.context = context;
        this.search_results_list = search_results_list;
        inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @Override
    public SearchResultItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search_result, parent, false);
        return new SearchResultItemHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultItemHolder holder, int position) {
        search_result = search_results_list.get(position);

        if (search_result.getFilename() != null) {
            holder.iv_place_text_in_search_box.setImageResource(R.drawable.search_up);
            holder.tv_search_item_name.setTextColor(Color.BLACK);
            holder.tv_search_item_name.setTextSize(18);
            holder.tv_search_item_name.setText(search_result.getName());
        } else {
            holder.iv_place_text_in_search_box.setImageDrawable(null);
            holder.tv_search_item_name.setText(search_result.getMenu());
            holder.tv_search_item_name.setTextColor(Color.BLUE);
            holder.tv_search_item_name.setTextColor(context.getResources().getColor(R.color.TEXT_BTN_COLOR));
            holder.tv_search_item_name.setTextSize(20);
        }
    }

    @Override
    public int getItemCount() {
        return search_results_list.size();
    }

    public interface SearchResultItemClickListener {
        void searchItemClickListener(View view, int position);
    }

    class SearchResultItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView iv_search, iv_place_text_in_search_box;
        TextView tv_search_item_name;

        public SearchResultItemHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            iv_search = (ImageView) itemView.findViewById(R.id.iv_search);
            iv_place_text_in_search_box = (ImageView) itemView.findViewById(R.id.iv_place_text_in_search_box);
            tv_search_item_name = (TextView) itemView.findViewById(R.id.tv_search_item_name);
        }

        @Override
        public void onClick(View v) {
            clickListener.searchItemClickListener(v, getAdapterPosition());
        }
    }
}