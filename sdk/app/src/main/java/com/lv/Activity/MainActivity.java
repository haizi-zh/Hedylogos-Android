package com.lv.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lv.R;
import com.lv.Utils.Config;
import com.lv.Utils.TimeUtils;
import com.lv.bean.ConversationBean;
import com.lv.bean.Message;
import com.lv.group.CreateSuccessListener;
import com.lv.group.GroupManager;
import com.lv.im.HandleImMessage;
import com.lv.im.IMClient;
import com.lv.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener, HandleImMessage.MessagerHandler {
    public static Button btn;
    Button test;
    ListView lv;
    List<HashMap<String, Object>> data;
    SimpleAdapter adapter = null;
    private SDKApplication application;
    List<ConversationBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IMClient.getInstance().initAckAndFetch();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        test = (Button) findViewById(R.id.btn_test);
        lv = (ListView) findViewById(R.id.listview);
        test.setOnClickListener(this);
        btn.setOnClickListener(this);
        application = (SDKApplication) getApplication();
        lv.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id)->{
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PrivateConversationActivity.class);
                intent.putExtra("friend_id", data.get(position).get("Friend_Id").toString());
                intent.putExtra("chatType", data.get(position).get("chatType").toString());
                if (data.get(position).get("conversation").toString()!=null) {
                    intent.putExtra("conversation", data.get(position).get("conversation").toString());
                }
                startActivity(intent);
            }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        HandleImMessage.registerMessageListener(this);
        if (Config.isDebug) {
            Log.i(Config.TAG, "CurrentUser " + User.getUser().getCurrentUser());
        }
        refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HandleImMessage.unregisterMessageListener(this);
    }

    private void refresh() {
        list = IMClient.getInstance().getConversationList();
        data = new ArrayList<>();
        for (int n = 0; n < list.size(); n++) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Friend_Id", list.get(n).getFriendId());
            hashMap.put("lastTime", TimeUtils.TimeStamp2Date(list.get(n).getLastChatTime()));
            hashMap.put("lastMsg", list.get(n).getLastMessage());
            hashMap.put("IsRead", list.get(n).getIsRead());
            hashMap.put("conversation", list.get(n).getConversation());
            hashMap.put("chatType", list.get(n).getChatType());
            data.add(hashMap);
        }
        adapter = new SimpleAdapter(this, data, R.layout.test_list_item, new String[]{"Friend_Id", "lastTime", "lastMsg", "IsRead"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4});
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                addFriendDialog();
                break;
            case R.id.btn_test:
                createDialog();
                break;
            default:
                break;
        }
    }
    private void addFriendDialog() {
        final EditText et=new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入好友id").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                et).setPositiveButton("确定",( dialog,  which)->{
                String friendId= et.getText().toString();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PrivateConversationActivity.class);
                intent.putExtra("friend_id", friendId);
                intent.putExtra("conversation", "");
                intent.putExtra("chatType", "single");
                startActivity(intent);
            }
        )
                .setNegativeButton("取消", null).show();


    }

    private void createDialog() {
        final EditText et=new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入群名称").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                et).setPositiveButton("确定", (DialogInterface dialog, int which)->{
                String groupName= et.getText().toString();
                List<Long> list1=new ArrayList<>();
                    list1.add(100001l);
                    list1.add(100002l);
                    list1.add(100000l);
                GroupManager.getGroupManager().createGroup(groupName,null,true,list1,new CreateSuccessListener() {
                    @Override
                    public void OnSuccess(String groupId,String conversation) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, PrivateConversationActivity.class);
                        intent.putExtra("friend_id", groupId);
                        intent.putExtra("conversation", conversation);
                        intent.putExtra("chatType", "group");
                        startActivity(intent);
                    }
                    @Override
                    public void OnFailed() {

                    }
                });
            }
        )
                .setNegativeButton("取消", null).show();
    }

    @Override
    public void onMsgArrive(Message m) {
        refresh();
    }
}
