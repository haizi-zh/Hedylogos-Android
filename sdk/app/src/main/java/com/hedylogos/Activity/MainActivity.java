package com.hedylogos.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hedylogos.Audio.MediaRecordFunc;
import com.hedylogos.R;
import com.hedylogos.bean.ConversationBean;
import com.hedylogos.dao.MessageBeanDaoHelper;
import com.hedylogos.data.MessageDB;
import com.hedylogos.net.UploadUtils;
import com.igexin.sdk.PushManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    public static Button btn;
    ImageView img;
    Button test;
    MessageBeanDaoHelper helper;
    ListView lv;
    MessageDB messageDB = new MessageDB("123");
    int i=0;
    List<ConversationBean> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        img = (ImageView) findViewById(R.id.img);
        test= (Button) findViewById(R.id.btn_test);
        lv = (ListView) findViewById(R.id.listview);
        test.setOnClickListener(this);
        btn.setOnClickListener(this);
        Log.d("GetuiSdkDemo", "initializing sdk...");
        PushManager.getInstance().initialize(this.getApplicationContext());
      //  Message message = new Message("1231");
      // System.out.print(JSON.toJSON(message).toString());
        helper = MessageBeanDaoHelper.getInstance(MainActivity.this);
        list= messageDB.getConversationList();
        List<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
        for (int n=0;n<list.size();n++){
            HashMap<String, Object> hashMap=new HashMap<String, Object>();
            hashMap.put("Friend_Id", list.get(n).getFriendId());
            hashMap.put("lastTime", list.get(n).getLastChatTime());
            hashMap.put("HASH", list.get(n).getHASH());
            data.add(hashMap);
        }
        lv.setAdapter(new SimpleAdapter(this,data,R.layout.test_list_item,new String[]{"Friend_Id","lastTime","HASH"},new int[]{R.id.tv1,R.id.tv2,R.id.tv3}));


       // MediaRecordFunc.getInstance().startRecordAndFile();
        new Thread(new Runnable() {
            @Override
            public void run() {

                long start=System.currentTimeMillis();
//                for (int i=50;i<150;i++) {
//                    for (int j = 0; j <100; j++) {
//                        messageDB.saveMsg(i+"", new MessageBean("hhh" + (j++),j));
//                    }
//                }
             //  messageDB.test_Insert();
                Long stop=System.currentTimeMillis();
                System.out.println("insert time:"+(stop-start));

                long istart=System.currentTimeMillis();
                List<ConversationBean> list= messageDB.getConversationList();
                Long istop=System.currentTimeMillis();
                System.out.println(" c time:"+(istop-istart)+"  list:"+list.size());
            }
        }).start();

        /*
        MessageBean msg =new MessageBean(0,0,0,"hhh99",1429608664009l,0,null,0,104);
        messageDB.saveMsg("458",msg);
        */
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                long num = helper.getTotalCount();
               // String content = helper.getDataById("1").getContent();
               /// System.out.println("num:" + num + "content:" + content);
                Intent intent = new Intent();
                // intent.setType("image/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult(intent, 1);
                intent.setClass(MainActivity.this, PrivateConversationActivity.class);
                startActivity(intent);
             break;
            case R.id.btn_test:
                MediaRecordFunc.getInstance().stopRecordAndFile();
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
