package com.educareapps.jsonreader;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by RK-REAZ on 7/20/2017.
 */

public class ItemAdapter extends BaseAdapter {
    ArrayList<ClipData.Item> items;
    Context context;
    LayoutInflater inflater = null;

    public ItemAdapter(Context context, ArrayList<ClipData.Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_cell, null);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tvItem);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvItem.setText("sfs");


        return convertView;
    }
    static class ViewHolder {
        TextView tvItem;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
