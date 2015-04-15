package com.hedylogos;

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

import com.hedylogos.net.UploadUtils;


public class MainActivity extends Activity implements View.OnClickListener{
    Button btn;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        img = (ImageView) findViewById(R.id.img);
        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
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
