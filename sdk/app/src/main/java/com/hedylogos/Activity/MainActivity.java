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

import com.hedylogos.R;
import com.hedylogos.bean.Message;
import com.hedylogos.dao.MessageBeanDaoHelper;
import com.hedylogos.net.UploadUtils;
import com.igexin.sdk.PushManager;


public class MainActivity extends Activity implements View.OnClickListener{
    public static Button btn;
    ImageView img;
    MessageBeanDaoHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        img = (ImageView) findViewById(R.id.img);
        btn.setOnClickListener(this);
        Log.d("GetuiSdkDemo", "initializing sdk...");
        PushManager.getInstance().initialize(this.getApplicationContext());


//        THDevOpenHelper helper = new THDevOpenHelper(MainActivity.this, "my-db", null);
//        SQLiteDatabase db =
//        DaoMaster daoMaster = new DaoMaster(db);
//      DaoSession daoSession = daoMaster.newSession();
       // daoSession.
//        THDevOpenHelper helper = new THDevOpenHelper(MainActivity.this, "notes-db", null);
//        SQLiteDatabase db = helper.getWritableDatabase();
//       DaoMaster daoMaster = new DaoMaster(db);
//        DaoSession daoSession = daoMaster.newSession();
//        MessageDao messageDao  = daoSession.getMessageDao();
//        Message msg =new Message();
//        messageDao.insertOrReplace(msg);
          helper=MessageBeanDaoHelper.getInstance(MainActivity.this);
        Message msg =new Message("1","2","23131313131","4","5","6","-1");
        helper.addData(msg);


    }

    @Override
    public void onClick(View v) {
       long num= helper.getTotalCount();
       String content= helper.getDataById("1").getContent();
        System.out.println("num:"+num+"content:"+content);
        Intent intent = new Intent();
       // intent.setType("image/*");
       // intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent, 1);
        intent.setClass(MainActivity.this,PrivateConversationActivity.class);
        startActivity(intent);
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
