package com.hedylogos.Activity;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hedylogos.R;
import com.hedylogos.Utils.CommonUtils;
import com.hedylogos.bean.Message;
import com.hedylogos.dao.MessageBeanDaoHelper;
import com.hedylogos.im.OnActivityMessageListener;

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
    private EditText composeZone;
    String currentName;
    String selfId;
    ListView chatList;
    ChatDataAdapter adapter;
    List<Message> messages = new LinkedList<Message>();
    MessageBeanDaoHelper helper;
    //Session session;
    int i = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.heartbeat);

        // this.setTitle(HTBApplication.lookupname(targetPeerId));

        // 您可以在这里读取本地的聊天记录，并且加载进来。
        // 　我们会在未来加入这些代码

        chatList = (ListView) this.findViewById(R.id.avoscloud_chat_list);
        helper = MessageBeanDaoHelper.getInstance(this);
        messages = helper.getAllData();
        adapter = new ChatDataAdapter(this, messages);
        chatList.setAdapter(adapter);
        chatList.setSelection(adapter.getCount() - 1);
        sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
        composeZone = (EditText) this.findViewById(R.id.chatText);
        //selfId =AVUser.getCurrentUser().getObjectId();
        //currentName = HTBApplication.lookupname(selfId);
        //session = SessionManager.getInstance(selfId);
        sendBtn.setOnClickListener(this);
//    if (!AVUtils.isBlankString(getIntent().getExtras()
//        .getString(Session.AV_SESSION_INTENT_DATA_KEY))) {
//      String msg = getIntent().getExtras().getString(Session.AV_SESSION_INTENT_DATA_KEY);
//      Message message = JSON.parseObject(msg, Message.class);
//      messages.add(message);
//      adapter.notifyDataSetChanged();
//    }
    }

    @Override
    public void onClick(View v) {
        String text = composeZone.getText().toString();

        if (TextUtils.isEmpty(text)) {
            return;
        }


        composeZone.getEditableText().clear();
        Message message = new Message("" + (i++), text);
        messages.add(message);
        helper.addData(message);
        adapter.notifyDataSetChanged();
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
            Message notify = new Message((i += 2) + "", "2", targetPeerId, "4", "5", "6", "-1");
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
        CommonUtils.Phonevibrator(PrivateConversationActivity.this);
    }

    @Override
    public void onMessage(String msg) {
        // ChatDemoMessage message = JSON.parseObject(msg, ChatDemoMessage.class);
        //messages.add(msg);
        //adapter.notifyDataSetChanged();
        // Message newmsg =new Message((i+=2)+"","2",msg,"4","5","6","-1");
        //  messages.add(newmsg);
        // helper.addData(newmsg);
        // adapter.notifyDataSetChanged();
    }
}
