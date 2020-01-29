package com.dam.daniel.playmatch.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dam.daniel.playmatch.R;
import com.dam.daniel.playmatch.models.ChatItem;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatItem> {

    Context context;
    List<ChatItem> items;


    public ChatListAdapter(Context context, ArrayList<ChatItem> dataArrayList){
        super(context, 0, dataArrayList);

        this.context = context;
        this.items = dataArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatListAdapter.ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.chat_item_list, parent, false);

            holder = new ChatListAdapter.ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);

        } else {
            holder = (ChatListAdapter.ViewHolder) convertView.getTag();
        }

        ChatItem productItems = items.get(position);

        holder.name.setText(productItems.getName());
        holder.message.setText(productItems.getMessage());
        Glide.with((View)holder.image).load(productItems.getImage()).apply(RequestOptions.circleCropTransform()).into(holder.image);

        return convertView;
    }

    private class ViewHolder {
        TextView message, name;
        ImageView image;
    }

}
