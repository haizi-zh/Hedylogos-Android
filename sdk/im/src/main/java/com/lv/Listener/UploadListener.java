package com.lv.Listener;

/**
 * Created by q on 2015/4/28.
 */
public interface UploadListener {

    public void onSucess(String fileUrl);

    public void onError(int errorCode, String msg);

    public void onProgress(int progress);

}
