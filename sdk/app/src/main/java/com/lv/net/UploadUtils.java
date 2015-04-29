package com.lv.net;

import android.graphics.Bitmap;

import com.lv.Utils.PictureUtil;
import com.lv.Utils.TimeUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Random;


public class UploadUtils {

    //private static final String fileName = "temp.jpg";
    //  private static final String imagepath = Environment.getExternalStorageDirectory().getPath() + "/SDK/image/" ;
    private static final String imagepath = "/mnt/sdcard/SDK/image/" ;
      private String KEY;
    private String TOKEN;

public interface tokenget{
         public void OnSuccess(String key,String token);
    }

    private UploadUtils() {

    }

    private static UploadUtils UploadUitls = null;

    private UploadManager uploadManager = new UploadManager();

    public static UploadUtils getInstance() {
        if (UploadUitls == null) {
            UploadUitls = new UploadUtils();
        }
        return UploadUitls;
    }

    public boolean saveBitmapToJpegFile(Bitmap bitmap, String filePath) {
        return PictureUtil.saveBitmapToJpegFile(bitmap, filePath, 75);
    }

    public void uploadImage(Bitmap bitmap,String sender,String receive,int msgType ,UploadListener listener) {
       File path=new File(imagepath);
        if (!path.exists())path.mkdirs();
        String imagepath1 = imagepath+ TimeUtils.getTimestamp()+"_image.jpeg";
        if (saveBitmapToJpegFile(bitmap, imagepath1))
        upload(imagepath1,sender,receive,msgType, listener);
        else System.out.println("文件出错");
    }

    public void uploadFile(File file, UploadListener listener) {
       // upload(file.getAbsolutePath(), listener);
    }
//    public void uploadbyte(byte[] data, QiniuUploadUitlsListener listener) {
//        saveBitmapToJpegFile(bitmap, tempJpeg);
//        upload(tempJpeg, listener);
//    }

    public void upload(final String filePath, final String sender, final String receive, final int msgType, final UploadListener listener) {
       System.out.println("filePath:"+filePath);
        final String fileUrlUUID = getFileUrlUUID();
       getToken(new tokenget() {
            @Override
            public void OnSuccess(String key, String token) {
                if (token == null) {
                    if (listener != null) {
                        listener.onError(-1, "token is null");
                    }
                    return;
                }
                HashMap<String,String> pamas=new HashMap<String, String>();
                pamas.put("x:sender",sender);
                pamas.put("x:msgType",msgType+"");
                pamas.put("x:receiver",receive);
                uploadManager.put(filePath, key, token, new UpCompletionHandler() {
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        System.out.println("debug:info = " + info + ",response = " + response);
                        if (info != null && info.statusCode == 200) {// 上传成功
                            if (listener != null) {
                                listener.onSucess(null);
                            }
                        } else {
                            if (listener != null) {
                                listener.onError(info.statusCode, info.error);
                            }
                        }
                    }
                }, new UploadOptions(pamas,null, false, new UpProgressHandler() {
                    public void progress(String key, double percent) {
                        if (listener != null) {
                            listener.onProgress((int) (percent * 100));
                        }
                    }
                }, null));
            }
        });


    }

    /**
     * 生成远程文件路径（全局唯一）
     *
     * @return filePath
     */
    private String getFileUrlUUID() {
        String filePath = android.os.Build.MODEL + "__" + System.currentTimeMillis() + "__" + (new Random().nextInt(500000))
                + "_" + (new Random().nextInt(10000));
        return filePath.replace(".", "0");
    }

    private String getRealUrl(String fileUrlUUID) {
        String filePath = "http://7xiktj.com1.z0.glb.clouddn.com/" + fileUrlUUID;
        return filePath;
    }

    /**
     * 获取token 本地生成
     *
     * @return token
     */
    public static void getToken(final tokenget listener) {
        String token=null;
          new Thread(new Runnable() {
              @Override
              public void run() {
                  JSONObject object= new JSONObject();
                  try {
                      object.put("action",1);

                  HttpPost post=new HttpPost("http://hedy.zephyre.me/upload/token-generator");
                  HttpResponse httpResponse = null;
                  StringEntity entity = new StringEntity(object.toString(),
                          HTTP.UTF_8);
                  entity.setContentType("application/json");
                  post.setEntity(entity);
                  httpResponse = new DefaultHttpClient().execute(post);
                      if (httpResponse.getStatusLine().getStatusCode()==200){
                          String result=EntityUtils.toString( httpResponse.getEntity());
                          JSONObject res =new JSONObject(result);
                          JSONObject obj=res.getJSONObject("result");
                          String key=obj.getString("key");
                          String token=obj.getString("token");
                          listener.OnSuccess(key,token);
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }

              }
          }).start();


//        Mac mac = new Mac("dNKSfnMLIBlWHQnmhre6nzL60QnTGuKK4U5aLjKU", "fPM-8HTDz1xRlOtgEqyhfBSeDlB3dixG9oP0dTfp");
//
//        PutPolicy putPolicy = new PutPolicy("lxpqyb");
//        putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
//        try {
//            String uptoken = putPolicy.token(mac);
//            System.out.println("debug:uptoken = " + uptoken);
//            return uptoken;
//        } catch (AuthException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}


