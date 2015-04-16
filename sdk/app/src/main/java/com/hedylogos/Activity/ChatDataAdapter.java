package com.hedylogos.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hedylogos.R;
import com.hedylogos.bean.Message;

import java.util.List;

public class ChatDataAdapter extends BaseAdapter {

    List<Message> messages;
    Context mContext;

    public ChatDataAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.mContext = context;
    }

    ;

    @Override
    public int getCount() {

        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int i=Integer.parseInt(messages.get(position).getType());
        System.out.println("getItemViewType:"+i);
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Message m = getItem(position);
        switch (getItemViewType(position)) {
            case -1:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_info, null);
                    holder = new ViewHolder();
                    holder.message = (TextView) convertView.findViewById(R.id.avoscloud_chat_demo_info);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                break;
            case 0:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
                    holder = new ViewHolder();
                    holder.message = (TextView) convertView.findViewById(R.id.avoscloud_chat_demo_message);
                    holder.username = (TextView) convertView.findViewById(R.id.avoscloud_chat_demo_user_id);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.username.setText(m.getId());
                break;
        }
        holder.message.setText(m.getContent());
        return convertView;
    }

    public class ViewHolder {
        TextView message;
        TextView username;
    }

}
