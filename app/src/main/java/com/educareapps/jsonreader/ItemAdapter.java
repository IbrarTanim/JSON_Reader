package com.educareapps.jsonreader;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.educareapps.jsonreader.dao.Item;

import java.util.ArrayList;

/**
 * Created by RK-REAZ on 7/20/2017.
 */

public class ItemAdapter extends BaseAdapter {
    Context context;
    ArrayList<Item> items;

    private static class ViewHolder {
        TextView tvItem;

        private ViewHolder() {
        }
    }

    public ItemAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View rowView = inflater.inflate(R.layout.item_cell, parent, false);
            convertView = inflater.inflate(R.layout.item_cell, parent, false);
//            convertView = inflater.inflate(R.layout.item_cell, null);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tvItem);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvItem.setText(String.valueOf(position + 1) + ". " + items.get(position).getUserText());
        return convertView;
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }
}
