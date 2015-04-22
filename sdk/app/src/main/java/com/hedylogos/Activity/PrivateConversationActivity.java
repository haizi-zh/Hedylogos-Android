package com.hedylogos.Activity;


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

import com.alibaba.fastjson.JSON;
import com.hedylogos.Audio.MediaRecordFunc;
import com.hedylogos.R;
import com.hedylogos.bean.IMessage;
import com.hedylogos.bean.Message;
import com.hedylogos.dao.MessageBeanDaoHelper;
import com.hedylogos.im.OnActivityMessageListener;
import com.hedylogos.im.SendMsgAsyncTask;
import com.hedylogos.net.UploadUtils;

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
    List<Message> messages = new LinkedList<Message>();
    MessageBeanDaoHelper helper;
    //Session session;
    long i = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.heartbeat);
        chatList = (ListView) this.findViewById(R.id.avoscloud_chat_list);
        input = (LinearLayout) findViewById(R.id.chat_input_wrapper);
        helper = MessageBeanDaoHelper.getInstance(this);
        messages = helper.getAllData();
        adapter = new ChatDataAdapter(this, messages);
        chatList.setAdapter(adapter);
        chatList.setSelection(adapter.getCount() - 1);
        sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
        Audiomsg = (Button) this.findViewById(R.id.Btn_AudioMsg);
        record = (ImageButton) this.findViewById(R.id.record);
        composeZone = (EditText) this.findViewById(R.id.chatText);
        sendBtn.setOnClickListener(this);
        Audiomsg.setOnClickListener(this);
        record.setOnTouchListener(new MyClickListener());
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
                //  Integer msgId, Integer sender, Integer receiver, String contents, Long timestamp, Stringconversation, Integer type
                IMessage message = new IMessage(1010l, 1000l, text);
                SendMsgAsyncTask.sendMessage(PrivateConversationActivity.this,message);
                Message msg = IMessagetoMessage(message);
                messages.add(msg);
                helper.addData(msg);
                adapter.notifyDataSetChanged();
                break;
            case R.id.Btn_AudioMsg:
                record.setVisibility(View.VISIBLE);
                input.setVisibility(View.GONE);
                break;
        }

    }

    private Message IMessagetoMessage(IMessage msg) {
        return new Message(i++, msg.getSender(), msg.getReceiver(), msg.getContents());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
        String targetPeerId = this.getIntent().getStringExtra(DATA_EXTRA_SINGLE_DIALOG_TARGET);
        if (targetPeerId != null) {
            Message notify = JSON.parseObject(targetPeerId, Message.class);
            messages.add(notify);
            helper.addData(notify);
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
        messages.add(msg);
        helper.addData(msg);
        adapter.notifyDataSetChanged();
        chatList.setSelection(messages.size() - 1);
       // CommonUtils.Phonevibrator(PrivateConversationActivity.this);
    }

    @Override
    public void onMessage(String msg) {
    }

    class MyClickListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                  String path= MediaRecordFunc.getInstance().stopRecordAndFile();
                    record.setVisibility(View.GONE);
                    input.setVisibility(View.VISIBLE);
                    UploadUtils.getInstance().upload(path,new UploadUtils.QiniuUploadUitlsListener() {
                        @Override
                        public void onSucess(String fileUrl) {
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

                    // TODO 异常放开，接下来一般做以下事情：删除录音文件

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
