package com.lv.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.lv.Listener.FetchListener;
import com.lv.Listener.SendMsgListener;
import com.lv.Listener.UploadListener;
import com.lv.Utils.Config;
import com.lv.Utils.CryptUtils;
import com.lv.Utils.TimeUtils;
import com.lv.bean.ConversationBean;
import com.lv.bean.IMessage;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.data.MessageDB;
import com.lv.net.HttpUtils;
import com.lv.net.UploadUtils;
import com.lv.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by q on 2015/4/21.
 */
public class IMClient {
    private boolean isBLOCK;
    private JSONArray acklist;
    private HashMap<String, Integer> lastMsgMap;
    private volatile HashMap<String, String> cidMap;
    private HashMap<String, String> user;
    private static final IMClient client = new IMClient();
    private MessageDB db;
    private List<MessageBean> messageList;
    private List<ConversationBean> convercationList;

    private IMClient() {
        convercationList = new ArrayList<ConversationBean>();
        cidMap = new HashMap<String, String>();
        user = new HashMap<String, String>();
        lastMsgMap = new HashMap<String, Integer>();
        acklist = new JSONArray();
        HandleImMessage.getInstance();
    }
    public static IMClient getInstance() {
        return client;
    }

    public void initDB() {
        db = MessageDB.getInstance();
    }

    public void init(Context context) {
        PushManager.getInstance().initialize(context.getApplicationContext());
    }

    public JSONArray getackList() {
        return acklist;
    }

    public int getackListsize() {
        return acklist.length();
    }

    public void add2ackList(String id) {
        acklist.put(id);
        System.out.println("ack list size:" + acklist.length());
        if (acklist.length() > 10) {
            HttpUtils.postack(acklist, User.getUser().getCurrentUser());
        }
    }

    public void clearackList() {
        acklist = new JSONArray();
    }

    public boolean isBLOCK() {
        return isBLOCK;
    }

    public void setBLOCK(boolean isBLOCK) {
        this.isBLOCK = isBLOCK;
    }

    public int getLastMsg(String fri_id) {
        if (lastMsgMap.get(fri_id) != null)
            return lastMsgMap.get(fri_id);
        else
            return -1;
    }

    public void setLastMsg(String fri_Id, int msgId) {
        if (!lastMsgMap.containsKey(fri_Id)) {
            lastMsgMap.put(fri_Id, -1);
        }
        int temp = lastMsgMap.get(fri_Id);
        if (temp > msgId) return;
        lastMsgMap.put(fri_Id, msgId);
    }
    public String getCid() {
        return cidMap.get("cid");
    }

    public void setCid(String cid) {
        cidMap.put("cid", cid);
    }

    public List<ConversationBean> getConversationList() {
//        if (convercationList.size()==0){
//            convercationList=db.getConversationList();
//        }
//        return convercationList;

        return db.getConversationList();
    }

    public void add2ConversationList(ConversationBean conversationBean) {
        convercationList.add(conversationBean);
    }

    public void updateReadStatus(String FriendId) {
        db.updateReadStatus(FriendId,0);
    }

    public void increaseUnRead(String FriendId){
        db.updateReadStatus(FriendId,1);
    }

    public List<MessageBean> getMessages(String friendId, int page) {
        return db.getAllMsg(friendId, page);
    }

    public void sendTextMessage(String text, String friendId,String conversation, SendMsgListener listen) {
        if (TextUtils.isEmpty(text)) return;
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.TEXT_MSG, text);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean);
        System.out.println("send  CurrentFriend "+friendId+" conversation"+conversation);
        SendMsgAsyncTask.sendMessage(User.getUser().getCurrentUser(),conversation, friendId, message, localId, listen);
    }

    public void sendAudioMessage(String path, String friendId, long durtime, UploadListener listener) {
        if (TextUtils.isEmpty(path)) return;
        JSONObject object = new JSONObject();
        try {
            object.put("path", path);
            object.put("durtime", durtime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.AUDIO_MSG, object.toString());
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean);
        UploadUtils.getInstance().upload(path, User.getUser().getCurrentUser(), friendId, Config.AUDIO_MSG, localId, listener);

    }

    public void sendImageMessage(String path, Bitmap bitmap, String friendId, UploadListener listener) {

        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.IMAGE_MSG, path);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean);
        UploadUtils.getInstance().uploadImage(bitmap, User.getUser().getCurrentUser(), friendId, Config.IMAGE_MSG, localId, listener);
    }

    public void updateMessage(String fri_ID, long LocalId, String msgId, String conversation, long timestamp, int status) {
        db.updateMsg(fri_ID, LocalId, msgId, conversation, timestamp, status);
    }

    private MessageBean imessage2Bean(IMessage message) {

        return new MessageBean(0, Config.STATUS_SENDING, message.getMsgType(), message.getContents(), TimeUtils.getTimestamp(), Config.TYPE_SEND, null, Long.parseLong(User.getUser().getCurrentUser()));
    }

    public void saveMessage(Message message) {
        MessageBean newMsg = Msg2Bean(message);
        db.saveMsg(newMsg.getSenderId() + "", newMsg);
        lastMsgMap.put(newMsg.getSenderId() + "", newMsg.getServerId());
        add2ackList(message.getId());
    }

    private MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), Config.STATUS_SUCCESS, msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }

    public void fetchNewMsg(FetchListener listener) {
        HttpUtils.FetchNewMsg(User.getUser().getCurrentUser(), listener);

    }

    public void initfetch() {
        System.out.println("fetchNewMsg IM");
        HttpUtils.FetchNewMsg(User.getUser().getCurrentUser(), new FetchListener() {
            @Override
            public void OnMsgArrive(List<Message> list) {
                for (Message msg : list) {
                    LazyQueue.getInstance().add2Temp(msg.getSenderId(), msg);
                }
                LazyQueue.getInstance().TempDequeue();
            }
        });
    }

    public int saveReceiveMsg(Message message) {
        int result = db.saveReceiveMsg(message.getSenderId() + "", Msg2Bean(message));
        if (result == 0) {
            setLastMsg(message.getSenderId() + "", message.getMsgId());
            add2ackList(message.getId());
        }
        return result;
    }

    public void saveMessages(List<Message> list) {
        List<MessageBean> list1 = new ArrayList<>();
        for (Message message : list) {
            list1.add(Msg2Bean(message));
            System.out.println(message.getMsgId());
        }
        db.saveMsgs(list1);
    }
    public void addGroup2Conversation(String groupId,String conversation){
        db.add2Conversion(Long.parseLong(groupId),TimeUtils.getTimestamp(), "chat_"+CryptUtils.getMD5String(groupId),-1,conversation);
    }
}

