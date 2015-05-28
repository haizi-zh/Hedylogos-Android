package com.lv.Activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lv.R;
import com.lv.Utils.TimeUtils;
import com.lv.bean.MessageBean;

import java.util.List;

public class ChatDataAdapter extends BaseAdapter {
private static final int AUDIO_MESSAGE=3;
    List<MessageBean> messages;
    Context mContext;
    MediaPlayer player=new MediaPlayer();

    public ChatDataAdapter(Context context, List<MessageBean> messages) {
        this.messages = messages;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public MessageBean getItem(int position) {
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
        int i =messages.get(position).getSendType();
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final MessageBean m = getItem(position);
        switch (getItemViewType(position)) {
            case 0:
//                if (m.getType()==2){
//                    if (convertView == null) {
//                        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message_send_audio, null);
//                        holder = new ViewHolder();
//                        holder.audio= (Button) convertView.findViewById(R.id.tv_send_audio);
//                        holder.time= (TextView) convertView.findViewById(R.id.tv_send_audio_timestamp);
//                        convertView.setTag(holder);
//                    } else {
//                        holder = (ViewHolder) convertView.getTag();
//                    }
//                    holder.audio.setText(m.getMessage());
//
//                    holder.audio.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//
//                                player.setDataSource(m.getMetadata());
//                                player.prepare();
//                                player.start();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    holder.time.setText(TimeUtils.TimeStamp2Date(m.getCreateTime()));
//                }
//                else {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message_send, null);
                        holder = new ViewHolder();
                        holder.message = (TextView) convertView.findViewById(R.id.tv_send_message);
                        holder.username = (TextView) convertView.findViewById(R.id.tv_send_id);
                        holder.time = (TextView) convertView.findViewById(R.id.tv_send_timestamp);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    holder.username.setText(m.getSenderId() + "");
                    holder.time.setText(TimeUtils.TimeStamp2Date(m.getCreateTime()));
                    holder.message.setText(m.getMessage());
                    System.out.println(m.getMessage()+" getLocalId "+m.getLocalId());
             //   }
                break;
            case 1:
                   if (convertView == null) {
                       convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
                       holder = new ViewHolder();
                       holder.message = (TextView) convertView.findViewById(R.id.avoscloud_chat_demo_message);
                       holder.username = (TextView) convertView.findViewById(R.id.avoscloud_chat_demo_user_id);
                       holder.time = (TextView) convertView.findViewById(R.id.avoscloud_feedback_timestamp);
                       convertView.setTag(holder);
                   } else {
                       holder = (ViewHolder) convertView.getTag();
                   }
                   holder.username.setText(m.getSenderId()+"");
                   holder.time.setText(TimeUtils.TimeStamp2Date(m.getCreateTime()));
                   holder.message.setText(m.getMessage());
                break;
        }
        return convertView;
    }
    public class ViewHolder {
        TextView message;
        TextView username;
        TextView time;
        Button audio;
    }

}
