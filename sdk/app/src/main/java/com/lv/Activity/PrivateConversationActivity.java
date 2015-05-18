package com.lv.Activity;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.lv.Listener.OnActivityMessageListener;
import com.lv.Listener.SendMsgListener;
import com.lv.Listener.UploadListener;
import com.lv.R;
import com.lv.Utils.CommonUtils;
import com.lv.Utils.Config;
import com.lv.Utils.TimeUtils;
import com.lv.bean.IMessage;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.group.GroupManager;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.lv.user.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PrivateConversationActivity extends Activity
        implements
        View.OnClickListener,
        OnActivityMessageListener ,
        HandleImMessage.MessagerHandler{
    public static final String DATA_EXTRA_SINGLE_DIALOG_TARGET = "single_target_peerId";
    private ImageButton sendBtn;
    Button Audiomsg;
    Button ImageMsg;
    ImageButton record;
    LinearLayout input;
    private EditText composeZone;
    static ListView chatList;
    static ChatDataAdapter adapter;
    List<Message> messages = new LinkedList<>();
    static List<MessageBean> msgs = new LinkedList<>();
    private String CurrentFriend;
    private String conversation;
    SDKApplication application;
    Long time;
    ActionBar actionBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.heartbeat);
        application = (SDKApplication) getApplication();
        initview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_add:
                createAddDialog();
                break;
            case R.id.action_member:
                GroupManager.getGroupManager().getGroupMembers(CurrentFriend);
                break;
            case R.id.action_info:
                GroupManager.getGroupManager().getGroupInformation(CurrentFriend);
                break;
            default:
              break;
        }
        return true;
    }

    private void createAddDialog() {
        final EditText et=new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入id").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                et).setPositiveButton("确定",(dialog, which)-> {
                String id= et.getText().toString();
                List<Long>list =new ArrayList<>();
                list.add(Long.parseLong(id));
                GroupManager.getGroupManager().addMembers(CurrentFriend,list,true);
            }
        )
                .setNegativeButton("取消", null).show();
    }

    private void initview() {
        actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        chatList = (ListView) this.findViewById(R.id.avoscloud_chat_list);
        input = (LinearLayout) findViewById(R.id.chat_input_wrapper);
        ImageMsg = (Button) findViewById(R.id.Btn_ImageMsg);
        sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
        Audiomsg = (Button) this.findViewById(R.id.Btn_AudioMsg);
        record = (ImageButton) this.findViewById(R.id.record);
        composeZone = (EditText) this.findViewById(R.id.chatText);
        sendBtn.setOnClickListener(this);
        Audiomsg.setOnClickListener(this);
        ImageMsg.setOnClickListener(this);
        record.setOnTouchListener(new MyClickListener());
        record.setOnClickListener(v->record.setVisibility(View.GONE));
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
               long localId=IMClient.getInstance().sendGroupTextMessage(text,CurrentFriend ,conversation, new SendMsgListener(){
                    @Override
                    public void onSuccess() {
                        if (Config.isDebug){
                            Log.i(Config.TAG,"发送成功");
                        }
                    }
                    @Override
                    public void onFailed(int code) {
                        System.out.println("failed code : " + code);
                    }
                });
                IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), CurrentFriend, 0, text);
                MessageBean messageBean = imessage2Bean(message);
                messageBean.setLocalId((int)localId);
                msgs.add(messageBean);
                adapter.notifyDataSetChanged();
                break;
            case R.id.Btn_AudioMsg:
                record.setVisibility(View.VISIBLE);
                input.setVisibility(View.GONE);
                break;
            case R.id.Btn_ImageMsg:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Config.CHOOSE_IMAGE_CODE);
                break;
        }
    }

    private static MessageBean imessage2Bean(IMessage message) {

        return new MessageBean(0, 0, message.getMsgType(), message.getContents(), TimeUtils.getTimestamp(), 0, null,Long.parseLong(User.getUser().getCurrentUser()));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
        CurrentFriend = getIntent().getStringExtra("friend_id");
        conversation = getIntent().getStringExtra("conversation");
        System.out.println("CurrentFriend "+CurrentFriend+" conversation"+conversation);
        HandleImMessage.registerMessageListener(this,conversation);
        if (Config.isDebug){
            Log.i(Config.TAG,"Current fri:" + CurrentFriend);
        }
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView tv1 = (TextView) findViewById(titleId);
        tv1.setTextColor(Color.BLACK);
        tv1.setText("好友：" + CurrentFriend);
        msgs = IMClient.getInstance().getMessages(CurrentFriend, 0);
        adapter = new ChatDataAdapter(this, msgs);
        chatList.setAdapter(adapter);
        String targetPeerId = this.getIntent().getStringExtra(DATA_EXTRA_SINGLE_DIALOG_TARGET);
        if (targetPeerId != null) {
            Message notify = JSON.parseObject(targetPeerId, Message.class);
            MessageBean messageBean = Msg2Bean(notify);
            msgs.add(messageBean);
            adapter.notifyDataSetChanged();
            //  chatList.setSelection(adapter.getCount() - 1);
            messages.add(notify);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
       HandleImMessage.unregisterMessageListener(this,conversation);
    }

    @Override
    public void onMessage(Message msg) {
        if (!conversation.equals(msg.getConversation())) {
            MessageBean messageBean = Msg2Bean(msg);
            messageBean.setSendType(1);
            Toast.makeText(PrivateConversationActivity.this, "有新消息！", Toast.LENGTH_SHORT).show();
        } else {
            MessageBean messageBean = Msg2Bean(msg);
            messageBean.setSendType(1);
            msgs.add(messageBean);
            adapter.notifyDataSetChanged();
            chatList.setSelection(adapter.getCount() - 1);
        }
    }

    public static MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), Config.STATUS_SUCCESS, msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }

    @Override
    public void onMessage(String msg) {
    }

    @Override
    public void onMsgArrive(Message m) {
        if (!CurrentFriend.equals(m.getSenderId() + "")) {
            MessageBean messageBean = Msg2Bean(m);
            messageBean.setSendType(1);
            Toast.makeText(PrivateConversationActivity.this, "有新消息！", Toast.LENGTH_SHORT).show();
        } else {
            MessageBean messageBean = Msg2Bean(m);
            messageBean.setSendType(1);
            msgs.add(messageBean);
            adapter.notifyDataSetChanged();
            chatList.setSelection(adapter.getCount() - 1);
        }
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
                    //final Long time = 0l;
                    try {
                         time = CommonUtils.getAmrDuration(new File(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MessageBean messageBean = new MessageBean("时长：" + time + "ms");
                    if (Config.isDebug){
                        Log.i(Config.TAG,"时长： " + time + "ms");
                    }
                    messageBean.setMetadata(path);
                    messageBean.setType(1);
                    messageBean.setSendType(0);
                    msgs.add(messageBean);
                    adapter.notifyDataSetChanged();
                    IMClient.getInstance().sendAudioMessage(path,CurrentFriend,time,new UploadListener() {
                        @Override
                        public void onSucess(String fileUrl) {
                            if (Config.isDebug){
                                Log.i(Config.TAG,"上传成功");
                            }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Config.CHOOSE_IMAGE_CODE) {
            //Log.i(TAG, "resultCode == RESULT_OK && requestCode == CHOOSE_IMAGE_CODE");
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                IMClient.getInstance().sendImageMessage(uri.getPath(),bitmap, CurrentFriend, new UploadListener() {
                    @Override
                    public void onSucess(String fileUrl) {
                        if (Config.isDebug){
                            Log.i(Config.TAG,"图片上传成功");
                        }
                    }

                    @Override
                    public void onError(int errorCode, String msg) {
                        if (Config.isDebug){
                            Log.i(Config.TAG,"errorCode :"+errorCode+" "+msg);
                        }
                    }
                    @Override
                    public void onProgress(int progress) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
