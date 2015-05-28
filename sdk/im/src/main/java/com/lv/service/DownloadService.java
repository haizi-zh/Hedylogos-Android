package com.lv.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.lv.Utils.Config;
import com.lv.Utils.CryptUtils;
import com.lv.bean.Message;
import com.lv.im.HandleImMessage;
import com.lv.user.User;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by q on 2015/5/4.
 */
public class DownloadService extends Service {
    private HashMap<String, Message> downlaodMap=new HashMap<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Config.ACTION_START.equals(intent.getAction())) {
            Message message = (Message)intent.getSerializableExtra("msg");
            if (!downlaodMap.containsKey(message.getUrl())) {
              //  DownloadTask task = new DownloadTask(message);
               // task.start();
                new DownloadTask1().execute(message);
                downlaodMap.put(message.getUrl(),message);
                if (Config.isDebug){
                    Log.i(Config.TAG,"开始下载！ "+message.getUrl());
                }
            }
            else {
                notice(message);
                if (Config.isDebug){
                    Log.i(Config.TAG,"已下载 ");
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownloadTask1 extends AsyncTask<Object,Void,Void>{
        private String url;
        private Message msg;
        private int msgType;
        @Override
        protected Void doInBackground(Object[] params) {

            msg= (Message) params[0];
            url= msg.getUrl();
            msgType=msg.getMsgType();
            HttpURLConnection conn = null;
            //OutputStream output = null;
            String newfilename=null;
            FileOutputStream output=null;
            try {
                URL downloadUrl = new URL(url);
                conn = (HttpURLConnection) downloadUrl.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (Config.isDebug){
                    Log.i(Config.TAG,"downlaod code: "+conn.getResponseCode());
                }
                if (conn.getResponseCode() == 200) {
                    int length = conn.getContentLength();
                    if (length < 0) {
                        return null;
                    }
                    File path = null;
                    File file= null;
                    StringBuffer sb = new StringBuffer();
                    String user=CryptUtils.getMD5String(User.getUser().getCurrentUser());
                    String name=CryptUtils.getMD5String(msg.getUrl());
                    switch (msgType) {
                        case 1:
                            path = new File(Config.DownLoadAudio_path +user);
                            file = new File(path, name+ ".amr");
                            newfilename=Config.DownLoadAudio_path +user+"/"+name+ ".amr";
                            break;
                        case 2:
                            path = new File(Config.DownLoadImage_path +user);
                            file = new File(path, name+ ".jpeg");
                            newfilename=Config.DownLoadImage_path +user+"/"+name+ ".jpeg";
                            break;
                    }

                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    if (file.exists()){
                        file.delete();
                      //  notice(msg);
                     //   if (Config.isDebug){
                      //      Log.i(Config.TAG,"已下载");
                     //   }
                    //    return null;
                    }
                    File newfile = new File(newfilename);
                    newfile.createNewFile();
                    InputStream input = null;
                    input = conn.getInputStream();
                    if (msgType==1){
                        output = new FileOutputStream(newfile);
                        byte buffer[] = new byte[4 * 1024];
                        while(input.read(buffer) != -1){
                            output.write(buffer);
                        }
                        output.flush();

                    }
                    if (msgType==2) {
                        Bitmap bm = BitmapFactory.decodeStream(input);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newfilename));
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        bos.flush();
                        bos.close();
                        bm.recycle();
                    }
                    else notice(msg);
                    if (Config.isDebug){
                        Log.i(Config.TAG, "下载完成");
                    }
                }
                notice(msg);

            } catch (Exception e) {
                e.printStackTrace();
                notice(msg);
                if (Config.isDebug){
                    Log.i(Config.TAG,"下载失败");
                }
            }finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    class DownloadTask extends Thread {
        private String url;
        private Message msg;
        private int msgType;

        DownloadTask(Message message) {
            this.url = message.getUrl();
            this.msgType = message.getMsgType();
            this.msg=message;
            downlaodMap.put(url,message);
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
          //  OutputStream output = null;
            String newfilename=null;
            try {
                URL downloadUrl = new URL(url);
                conn = (HttpURLConnection) downloadUrl.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (Config.isDebug){
                    Log.i(Config.TAG,"downlaod code: "+conn.getResponseCode() );
                }
                if (conn.getResponseCode() == 200) {
                  int length = conn.getContentLength();
                    if (length < 0) {
                        return;
                    }
                    File path = null;
                    File file= null;
                    String user=CryptUtils.getMD5String(User.getUser().getCurrentUser());
                    String name=CryptUtils.getMD5String(msg.getUrl());
                    switch (msgType) {
                        case 1:
                            path = new File(Config.DownLoadAudio_path +user);
                            file = new File(path, name+ ".amr");
                            newfilename=Config.DownLoadAudio_path +user+name+ ".amr";
                            break;
                        case 2:
                            path = new File(Config.DownLoadImage_path +user);
                            file = new File(path, name+ ".jpeg");
                            newfilename=Config.DownLoadImage_path +user+"/"+name+ ".jpeg";
                            break;
                    }

                    if (!path.exists()) {
                        path.mkdirs();
                    }
                   if (file.exists()){
                       file.delete();
                      //notice(msg);
                      // System.out.println("已下载 ");
                    //  return;
                   }
                    System.out.println(newfilename);
                    File newfile = new File(newfilename);
                    if (!newfile.createNewFile())
                        Log.i(Config.TAG,"DownloadService failed to createFile!");
                    InputStream input = conn.getInputStream();

//                    if (newfilename != null) {
//                        output = new FileOutputStream(newfile);
//                    }
//                    ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
//                    byte[] buffer = new byte[1024];
//                    int l=0;
//                    while ((l=input.read(buffer))!= -1) {
//                        outputStream.write(buffer,0,l);
//                    }
                    Bitmap bm= BitmapFactory.decodeStream(input);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newfilename));
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bos.flush();
                    bos.close();
                    bm.recycle();
                    if (Config.isDebug) {
                        System.out.println("下载完成！");
                    }
                }
                notice(msg);
            } catch (Exception e) {
                e.printStackTrace();
                notice(msg);
                System.out.println("下载失败！");
            }finally {
                      //  output.close();
                    if (conn != null) {
                        conn.disconnect();
                    }
            }
        }
    }
    private void notice(Message msg){
        android.os.Message message= android.os.Message.obtain();
        message.obj=msg;
        message.what=Config.DOWNLOAD_SUCCESS;
        HandleImMessage.handler.sendMessage(message);
    }
}