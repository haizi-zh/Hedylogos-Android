package com.lv.im;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.lv.Utils.Config;
import com.lv.Utils.TimeUtils;
import com.lv.bean.ConversationBean;
import com.lv.bean.IMessage;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.data.MessageDB;
import com.lv.net.HttpUtils;
import com.lv.net.LoginSuccessListen;
import com.lv.net.UploadListener;
import com.lv.net.UploadUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by q on 2015/4/21.
 */
public class IMClient {
    private boolean isBLOCK ;
    public static final String SP_FILE_NAME = "push_msg_sp";
    private static String CurrentUser;
    private ArrayList<String> acklist;
    private HashMap<String,Integer> lastMsgMap;
    private HashMap<String,String> cidMap;
    private HashMap<String,String> user;
    private static final IMClient client=new IMClient();
    private MessageDB db;
    private IMClient(){
        cidMap =new HashMap<String, String>();
        user =new HashMap<String, String>();
        lastMsgMap=new HashMap<String, Integer>();
        acklist=new ArrayList<String>();
    }
    public static IMClient getInstance(){
        return client;
    }
    public String getCurrentUser() {
        return CurrentUser;
    }

    public void setCurrentUser(String currentUser) {
       CurrentUser=currentUser;
    }
    public void initDB(){
        db=new MessageDB(CurrentUser);
    }
    public ArrayList<String> getackList() {
        return acklist;
    }

    public int getackListsize() {
        return acklist.size();
    }

    public void add2ackList(String id) {
        acklist.add(id);
    }

    public void clearackList() {
        acklist.clear();
    }

    public boolean isBLOCK() {
        return isBLOCK;
    }

    public void setBLOCK(boolean isBLOCK) {
        this.isBLOCK = isBLOCK;
    }

    public int getLastMsg(String fri_id) {
        if (lastMsgMap.get(fri_id)!=null)
        return lastMsgMap.get(fri_id);
        else
        return -1;
    }

    public void setLastMsg (String fri_Id,int msgId) {
      //  int temp=lastMsgMap.get(fri_Id);
      //  if (temp>msgId)return;
        lastMsgMap.put(fri_Id,msgId);
    }

    public void Login(String UserId,LoginSuccessListen listen){
        HttpUtils.login(UserId,listen);
    }

    public String getCid() {
        return cidMap.get("cid");
    }

    public void setCid(String cid) {
        cidMap.put("cid",cid);
    }
    public List<ConversationBean> getConversationList(){
        return db.getConversationList();
    }
    public List<MessageBean> getMessages(String friendId,int page){
        return db.getAllMsg(friendId,page);
    }

    public void sendTextMessage(String text,int friendId, SendMsgListen listen) {
        if (TextUtils.isEmpty(text))return;
        IMessage message = new IMessage(Integer.parseInt(CurrentUser),friendId, Config.TEXT_MSG, text);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId+"", messageBean);
        SendMsgAsyncTask.sendMessage(CurrentUser,friendId+"", message,localId,listen);
    }
    private MessageBean imessage2Bean(IMessage message) {

        return new MessageBean(0, Config.STATUS_SENDING, message.getMsgType(), message.getContents(), TimeUtils.getTimestamp(), Config.TYPE_SEND, null, Long.parseLong(CurrentUser));
    }
    public void saveMessage(Message message){
        MessageBean newMsg=Msg2Bean(message);
        db.saveMsg(newMsg.getSenderId()+"",newMsg);
        lastMsgMap.put(newMsg.getSenderId()+"",newMsg.getServerId());
        add2ackList(message.getId());
    }
    private MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), Config.STATUS_SUCCESS, msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }
    public void fetchNewMsg(){
            HttpUtils.FetchNewMsg(CurrentUser);

    }
    public void UploadImage(Bitmap bitmap,UploadListener listener){
        UploadUtils.getInstance().uploadImage(bitmap,"3","2",2,listener);
    }
    public void saveMessages(List<Message> list){
        List<MessageBean> list1 =new ArrayList<MessageBean>();
        for (Message message:list){
          list1.add(Msg2Bean(message));
            System.out.println(message.getMsgId());
        }
        db.saveMsgs(list1);

    }
    public void test(){
        System.out.println("c:"+CurrentUser);
    }
}

