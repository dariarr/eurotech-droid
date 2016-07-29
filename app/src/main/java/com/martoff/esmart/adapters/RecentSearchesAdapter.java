package com.martoff.esmart.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martoff.esmart.R;
import com.martoff.esmart.pojo.ItemRecentSearches;

import java.util.ArrayList;

/**
 * Created by Safeer on 29-May-16.
 */
public class RecentSearchesAdapter extends RecyclerView.Adapter<RecentSearchesAdapter.RecentSearchesItemHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<ItemRecentSearches> recent_searches_list;
    ItemRecentSearches recent_search_item;
    String TAG = "RecentSearchesAdapter";

    public RecentSearchesAdapter(Context context, ArrayList<ItemRecentSearches> recent_searches_list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.recent_searches_list = recent_searches_list;
    }

    @Override
    public RecentSearchesItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recent_searches, parent, false);
        return new RecentSearchesItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentSearchesItemHolder holder, int position) {
        recent_search_item = recent_searches_list.get(position);

        holder.tv_search_name.setText(recent_search_item.getName());
        switch (recent_search_item.getEmail()) {
            case 0:
                holder.tv_recent_searches_alert_type.setText(R.string.no_email_alert_0);
                break;

            case 1:
                holder.tv_recent_searches_alert_type.setText(R.string.daily_alerts_1);
                break;

            case 2:
                holder.tv_recent_searches_alert_type.setText(R.string.weekly_alerts_2);
                break;

            case 3:
                holder.tv_recent_searches_alert_type.setText(R.string.fortnightly_alerts_3);
                break;

            case 4:
                holder.tv_recent_searches_alert_type.setText(R.string.monthly_alert_4);
                break;

            default:
                holder.tv_recent_searches_alert_type.setText(R.string.no_email_alert_0);
        }
    }

    @Override
    public int getItemCount() {
        return recent_searches_list.size();
    }

    class RecentSearchesItemHolder extends RecyclerView.ViewHolder {

        TextView tv_search_name, tv_recent_searches_alert_type;

        public RecentSearchesItemHolder(View itemView) {
            super(itemView);

            tv_search_name = (TextView) itemView.findViewById(R.id.tv_recent_search_name);
            tv_recent_searches_alert_type = (TextView) itemView.findViewById(R.id.tv_recent_searches_alert_type);
        }
    }
}