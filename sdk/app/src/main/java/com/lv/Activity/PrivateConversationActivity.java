package com.lv.Activity;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lv.Audio.MediaRecordFunc;
import com.lv.R;
import com.lv.Utils.CommonUtils;
import com.lv.Utils.TimeUtils;
import com.lv.bean.IMessage;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.data.MessageDB;
import com.lv.im.OnActivityMessageListener;
import com.lv.im.SendMsgAsyncTask;
import com.lv.net.UploadUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PrivateConversationActivity extends Activity
        implements
        View.OnClickListener,
        OnActivityMessageListener {
    public static final String DATA_EXTRA_SINGLE_DIALOG_TARGET = "single_target_peerId";
    public static final String Activityid = "PrivateConversationActivity";
    String targetPeerId;
    private ImageButton sendBtn;
    Button Audiomsg;
    ImageButton record;
    LinearLayout input;
    private EditText composeZone;
    String currentName;
    String selfId;
    ListView chatList;
    ChatDataAdapter adapter;
    TextView tv;
    List<Message> messages = new LinkedList<Message>();
    List<MessageBean> msgs = new LinkedList<MessageBean>();
    private String CurrentFriend;
    private String CurrentUser;
    SDKApplication application;
    long i = 2;
    MessageDB db ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.heartbeat);
        application=(SDKApplication) getApplication();
        CurrentUser=application.getCurrentUser();
        db = new MessageDB(CurrentUser);
        initview();
        CurrentFriend = getIntent().getStringExtra("friend_id");
        tv.setText("好友：" + CurrentFriend);
    }
    private void initview() {
        chatList = (ListView) this.findViewById(R.id.avoscloud_chat_list);
        input = (LinearLayout) findViewById(R.id.chat_input_wrapper);
        tv = (TextView) findViewById(R.id.top);
        sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
        Audiomsg = (Button) this.findViewById(R.id.Btn_AudioMsg);
        record = (ImageButton) this.findViewById(R.id.record);
        composeZone = (EditText) this.findViewById(R.id.chatText);
        sendBtn.setOnClickListener(this);
        Audiomsg.setOnClickListener(this);
        record.setOnTouchListener(new MyClickListener());
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                String text = composeZone.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                composeZone.getEditableText().clear();
                IMessage message = new IMessage(2, 1, 1, text);
                MessageBean messageBean = imessage2Bean(message);
                System.out.println(message.toString());
                SendMsgAsyncTask.sendMessage(CurrentUser,CurrentFriend, message);
               // SendMsgAsyncTask.sendMsg(PrivateConversationActivity.this, message);
                // MessageBean msg = new MessageBean(text);
                msgs.add(messageBean);
                db.saveMsg(CurrentFriend, messageBean);
                adapter.notifyDataSetChanged();
                break;
            case R.id.Btn_AudioMsg:
                record.setVisibility(View.VISIBLE);
                input.setVisibility(View.GONE);
                break;
        }
    }

    private MessageBean imessage2Bean(IMessage message) {

        return new MessageBean(0, 0, message.getMsgType(), message.getContents(), TimeUtils.getTimestamp(), 0, null, 2);
    }

    private Message IMessagetoMessage(IMessage msg) {
        //return new Message(i++, msg.getSender(), msg.getReceiver(), msg.getContents());
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
        msgs = db.getAllMsg(CurrentFriend, 0);
        adapter = new ChatDataAdapter(this, msgs);
        chatList.setAdapter(adapter);
        chatList.setSelection(adapter.getCount() - 1);
        String targetPeerId = this.getIntent().getStringExtra(DATA_EXTRA_SINGLE_DIALOG_TARGET);
        if (targetPeerId != null) {
            Message notify = JSON.parseObject(targetPeerId, Message.class);
            MessageBean messageBean = Msg2Bean(notify);
            msgs.add(messageBean);
            db.saveMsg(CurrentFriend, messageBean);
            adapter.notifyDataSetChanged();
            chatList.setSelection(adapter.getCount() - 1);
            messages.add(notify);
            adapter.notifyDataSetChanged();
        }
        IMMessageReceiver.registerSessionListener(Activityid, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        IMMessageReceiver.unregisterSessionListener(Activityid);
    }

    @Override
    public void onMessage(Message msg) {
        application.add2ackLit(msg.getId());
        if (application.getLastMsg(CurrentFriend)!=msg.getMsgId())application.setBLOCK(true);

        if (application.getackListsize()>=20){
            SendMsgAsyncTask.postack(application.getackList(),CurrentUser);
        }

        if (!CurrentFriend.equals(msg.getSenderId() + "")) {
            MessageBean messageBean = Msg2Bean(msg);
            messageBean.setSendType(1);
            db.saveMsg(messageBean.getSenderId() + "", messageBean);
            Toast.makeText(PrivateConversationActivity.this, "有新消息！", Toast.LENGTH_SHORT).show();
        } else {
            MessageBean messageBean = Msg2Bean(msg);
            messageBean.setSendType(1);
            msgs.add(messageBean);
            db.saveMsg(CurrentFriend, messageBean);
            adapter.notifyDataSetChanged();
            chatList.setSelection(adapter.getCount() - 1);
        }
    }

    private MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), 0, 0, msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }

    @Override
    public void onMessage(String msg) {
        Toast.makeText(PrivateConversationActivity.this, "有新消息:" + msg, Toast.LENGTH_SHORT).show();
    }

    class MyClickListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    MediaRecordFunc.getInstance().startRecordAndFile();
                    break;
                case MotionEvent.ACTION_UP:
                    final String path = MediaRecordFunc.getInstance().stopRecordAndFile();
                    record.setVisibility(View.GONE);
                    input.setVisibility(View.VISIBLE);
                    UploadUtils.getInstance().upload(path, new UploadUtils.QiniuUploadUitlsListener() {
                        @Override
                        public void onSucess(String fileUrl) {
                            Long time = 0l;
                            try {
                                time = CommonUtils.getAmrDuration(new File(path));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            MessageBean messageBean = new MessageBean("时长：" + time + "ms");
                            messageBean.setMetadata(path);
                            messageBean.setType(2);
                            messageBean.setSendType(0);
                            db.saveMsg(CurrentFriend, messageBean);
                            msgs.add(messageBean);
                            adapter.notifyDataSetChanged();
                            System.out.println("上传成功！");
                        }

                        @Override
                        public void onError(int errorCode, String msg) {
                        }

                        @Override
                        public void onProgress(int progress) {
                        }
                    });
                    break;

                case MotionEvent.ACTION_CANCEL:
                    // 停止录音
                    MediaRecordFunc.getInstance().stopRecordAndFile();
                    break;

                default:
                    break;
            }
            return false;
        }

    }

}
