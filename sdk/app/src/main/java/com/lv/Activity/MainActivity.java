package com.lv.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lv.Listener.OnActivityMessageListener;
import com.lv.R;
import com.lv.Utils.TimeUtils;
import com.lv.bean.ConversationBean;
import com.lv.bean.Message;
import com.lv.im.IMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener ,OnActivityMessageListener{
    public static Button btn;
    ImageView img;
    Button test;
    ListView lv;
    List<HashMap<String, Object>> data;
    SimpleAdapter adapter = null;
    private String currentuser;
    private SDKApplication application;
    static int i = 1;
    List<ConversationBean> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IMClient.getInstance().initfetch();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        test = (Button) findViewById(R.id.btn_test);
        lv = (ListView) findViewById(R.id.listview);
        test.setOnClickListener(this);
        btn.setOnClickListener(this);
        application = (SDKApplication) getApplication();
        currentuser = (application.getCurrentUser());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PrivateConversationActivity.class);
                intent.putExtra("friend_id", data.get(position).get("Friend_Id").toString());
                System.out.println("M fid" + data.get(position).get("Friend_Id").toString());
                startActivity(intent);
            }
        });
        IMMessageReceiver.registerSessionListener("PrivateConversationActivity", this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("CurrentUser " + IMClient.getInstance().getCurrentUser());
        refresh();
    }
    private void refresh(){
        list= IMClient.getInstance().getConversationList();
        data = new ArrayList<HashMap<String, Object>>();
        for (int n = 0; n < list.size(); n++) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("Friend_Id", list.get(n).getFriendId());
            hashMap.put("lastTime", TimeUtils.TimeStamp2Date(list.get(n).getLastChatTime()));
            hashMap.put("HASH", list.get(n).getHASH());
            application.setLastMsg(list.get(n).getFriendId() + "", list.get(n).getLast_rev_msgId());
            data.add(hashMap);
        }
        adapter = new SimpleAdapter(this, data, R.layout.test_list_item, new String[]{"Friend_Id", "lastTime", "HASH"}, new int[]{R.id.tv1, R.id.tv2, R.id.tv3});
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PrivateConversationActivity.class);
                intent.putExtra("friend_id", "1");
                startActivity(intent);
                break;
            case R.id.btn_test:
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, AEStest.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
    @Override
    public void onMessage(Message msg) {
        refresh();
    }

    @Override
    public void onMessage(String msg) {
    }
}
