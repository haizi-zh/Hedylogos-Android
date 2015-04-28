package com.lv.net;

import android.graphics.Bitmap;
import android.os.Environment;

import com.lv.Utils.PictureUtil;
import com.lv.Utils.TimeUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Random;


public class UploadUtils {

    //private static final String fileName = "temp.jpg";
      private static final String imagepath = Environment.getExternalStorageDirectory().getPath() + "/SDK/image/" ;




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

    public void uploadImage(Bitmap bitmap, UploadListener listener) {
        String image = imagepath+ TimeUtils.getTimestamp()+"_image.jpeg";
        saveBitmapToJpegFile(bitmap, image);
        upload(image, listener);
    }

    public void uploadFile(File file, UploadListener listener) {
        upload(file.getAbsolutePath(), listener);
    }
//    public void uploadbyte(byte[] data, QiniuUploadUitlsListener listener) {
//        saveBitmapToJpegFile(bitmap, tempJpeg);
//        upload(tempJpeg, listener);
//    }

    public void upload(String filePath, final UploadListener listener) {
        final String fileUrlUUID = getFileUrlUUID();
        String token = getToken();
        if (token == null) {
            if (listener != null) {
                listener.onError(-1, "token is null");
            }
            return;
        }
        uploadManager.put(filePath, fileUrlUUID, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo info, JSONObject response) {
                System.out.println("debug:info = " + info + ",response = " + response);
                if (info != null && info.statusCode == 200) {// 上传成功
                    String fileRealUrl = getRealUrl(fileUrlUUID);
                    System.out.println("debug:fileRealUrl = " + fileRealUrl);
                    if (listener != null) {
                        listener.onSucess(fileRealUrl);
                    }
                } else {
                    if (listener != null) {
                        listener.onError(info.statusCode, info.error);
                    }
                }
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            public void progress(String key, double percent) {
                if (listener != null) {
                    listener.onProgress((int) (percent * 100));
                }
            }
        }, null));

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
    private String getToken() {
        /**
         HttpClient client = new DefaultHttpClient();
         StringBuilder builder = new StringBuilder();
         HttpGet myget = new HttpGet(URL);
         try {
         HttpResponse response = client.execute(myget);
         BufferedReader reader = new BufferedReader(new InputStreamReader(
         response.getEntity().getContent()));
         for (String s = reader.readLine(); s != null; s = reader.readLine()) {
         builder.append(s);
         }
         return builder.toString();
         } catch (Exception e) {
         Log.i("url response", "false");
         e.printStackTrace();
         return null;
         **/
        Mac mac = new Mac("dNKSfnMLIBlWHQnmhre6nzL60QnTGuKK4U5aLjKU", "fPM-8HTDz1xRlOtgEqyhfBSeDlB3dixG9oP0dTfp");

        PutPolicy putPolicy = new PutPolicy("lxpqyb");
        putPolicy.returnBody = "{\"name\": $(fname),\"size\": \"$(fsize)\",\"w\": \"$(imageInfo.width)\",\"h\": \"$(imageInfo.height)\",\"key\":$(etag)}";
        try {
            String uptoken = putPolicy.token(mac);
            System.out.println("debug:uptoken = " + uptoken);
            return uptoken;
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}


