package com.lv.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lv.R;
import com.lv.Utils.TimeUtils;
import com.lv.bean.ConversationBean;
import com.lv.im.IMClient;
import com.lv.net.UploadUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    public static Button btn;
    ImageView img;
    Button test;
    ListView lv;
    List<HashMap<String, Object>> data;
    SimpleAdapter adapter =null;
    List<ConversationBean> list;
    private String currentuser;
    private SDKApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        img = (ImageView) findViewById(R.id.img);
        test = (Button) findViewById(R.id.btn_test);
        lv = (ListView) findViewById(R.id.listview);
        test.setOnClickListener(this);
        btn.setOnClickListener(this);
        application=(SDKApplication) getApplication();
        currentuser = (application.getCurrentUser());

        Log.d("GetuiSdkDemo", "initializing sdk...");
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
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("CurrentUser " + IMClient.getInstance().getCurrentUser());
        List<ConversationBean> list= IMClient.getInstance().getConversationList();
        data=new ArrayList<HashMap<String,Object>>();
        for (int n=0;n<list.size();n++){
            HashMap<String, Object> hashMap=new HashMap<String, Object>();
            hashMap.put("Friend_Id", list.get(n).getFriendId());
            hashMap.put("lastTime", TimeUtils.TimeStamp2Date( list.get(n).getLastChatTime()));
            hashMap.put("HASH", list.get(n).getHASH());
            application.setLastMsg(list.get(n).getFriendId()+"",list.get(n).getLast_rev_msgId());
            data.add(hashMap);
        }
        adapter=new SimpleAdapter(this,data,R.layout.test_list_item,new String[]{"Friend_Id","lastTime","HASH"},new int[]{R.id.tv1,R.id.tv2,R.id.tv3});
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PrivateConversationActivity.class);
                intent.putExtra("friend_id","1");
                startActivity(intent);
             break;
            case R.id.btn_test:
                Intent intent1= new Intent();
                intent1.setClass(MainActivity.this,AEStest.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Log.i("tag", "resultCode == RESULT_OK && requestCode == CHOOSE_IMAGE_CODE");
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                uploadBitmap(bitmap);
                Log.i("tag", "uploadBitmap(bitmap); ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传从相册选择的图片
     *
     * @param result
     */
    private void uploadBitmap(Bitmap result) {
        UploadUtils.getInstance().uploadImage(result, new UploadUtils.QiniuUploadUitlsListener() {

            public void onSucess(String fileUrl) {

                System.out.print("上传图片成功");
            }

            public void onProgress(int progress) {
            }

            public void onError(int errorCode, String msg) {
                System.out.print("errorCode=" + errorCode + ",msg=" + msg);
            }
        });
    }
}
