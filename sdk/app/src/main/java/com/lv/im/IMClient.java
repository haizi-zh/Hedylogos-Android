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
import java.util.Iterator;
import java.util.List;

/**
 * Created by q on 2015/4/21.
 */
public class IMClient {
    private boolean isBLOCK;
    private JSONArray acklist;
    private HashMap<String, Integer> lastMsgMap;
    private volatile HashMap<String, String> cidMap;
    private static final IMClient client = new IMClient();
    private MessageDB db;
    private static List<ConversationBean> convercationList;

    private IMClient() {
        convercationList = new ArrayList<>();
        cidMap = new HashMap<>();
        lastMsgMap = new HashMap<>();
        acklist = new JSONArray();
        HandleImMessage.getInstance();
    }

    public static IMClient getInstance() {
        return client;
    }

    public void initDB() {
        db = MessageDB.getInstance();
        MessageDB.getInstance().init();
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
//            HttpUtils.FetchNewMsg(User.getUser().getCurrentUser(), (list) -> {
//                for (Message msg : list) {
//                    LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
//                }
//                LazyQueue.getInstance().TempDequeue();
//            });
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

    public int getLastMsg(String conversation) {
        if (lastMsgMap.get(conversation) != null)
            return lastMsgMap.get(conversation);
        else
            return -1;
    }

    public void setLastMsg(String conversation, int msgId) {
        if (!lastMsgMap.containsKey(conversation)) {
            lastMsgMap.put(conversation, -1);
        }
        int temp = lastMsgMap.get(conversation);
        if (temp > msgId) return;
        lastMsgMap.put(conversation, msgId);
    }

    public String getCid() {
        return cidMap.get("cid");
    }

    public void setCid(String cid) {
        cidMap.put("cid", cid);
    }

    /**
     *
     * @return List<ConversationBean>
     */
    public List<ConversationBean> getConversationList() {
//        if (convercationList.size()==0){
//            convercationList=db.getConversationList();
//        }

      convercationList = db.getConversationList();
        return convercationList;
    }

    public List<ConversationBean> getConversationListCache() {
        return convercationList;
    }

    public void add2ConversationList(ConversationBean conversationBean) {
        convercationList.add(conversationBean);
    }

    public void updateReadStatus(String conversation) {
        db.updateReadStatus(conversation, 0);
        System.out.println("updateReadStatus");
    }

    public void increaseUnRead(String conversation) {
        db.updateReadStatus(conversation, 1);
        System.out.println("increaseUnRead");
    }

    public List<MessageBean> getMessages(String friendId, int page) {
        return db.getAllMsg(friendId, page);
    }

    /**
     * 发送文本消息
     *
     * @param text         消息内容
     * @param friendId     friendId
     * @param conversation 会话Id
     * @param listen       listener
     * @return MessageBean
     */
    public MessageBean sendTextMessage(String text, String friendId, String conversation, SendMsgListener listen, String chatType) {
        if (TextUtils.isEmpty(text)) return null;
        if ("0".equals(conversation)) conversation = null;
        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.TEXT_MSG, text);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean,chatType);
        System.out.println("send  CurrentFriend " + friendId + " conversation" + conversation);
        SendMsgAsyncTask.sendMessage(conversation, friendId, message, localId, listen, chatType);
        return baseMessage(text,friendId,localId);
    }
    public MessageBean baseMessage(String text,String friendId,long localId){
       MessageBean messageBean= new MessageBean(0,1,0,text,TimeUtils.getTimestamp(),0,null,Long.parseLong(friendId));
        messageBean.setLocalId((int)localId);
        return messageBean;
    }

    public MessageBean sendSingleTextMessage(String text, String friendId, String conversation, SendMsgListener listen) {
        return sendTextMessage(text, friendId, conversation, listen, "single");
    }

    public MessageBean sendGroupTextMessage(String text, String friendId, String conversation, SendMsgListener listen) {
        return sendTextMessage(text, friendId, conversation, listen, "group");
    }

    /**
     * 发送语音消息
     *
     * @param path     路径
     * @param friendId friendId
     * @param durtime  持续时间
     * @param listener listener
     * @param chatTpe  聊天类型
     */
    public void sendAudioMessage(String path, String friendId, long durtime, UploadListener listener,String chatTpe) {
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
        long localId = db.saveMsg(friendId, messageBean,chatTpe);
        UploadUtils.getInstance().upload(path, User.getUser().getCurrentUser(), friendId, Config.AUDIO_MSG, localId, listener,chatTpe);

    }

    /**
     * 发送图片消息
     *
     * @param path     路径
     * @param bitmap   图片
     * @param friendId friendId
     * @param listener listener
     */
    public void sendImageMessage(String path, Bitmap bitmap, String friendId, UploadListener listener,String chatTpe) {

        IMessage message = new IMessage(Integer.parseInt(User.getUser().getCurrentUser()), friendId, Config.IMAGE_MSG, path);
        MessageBean messageBean = imessage2Bean(message);
        long localId = db.saveMsg(friendId, messageBean,chatTpe);
        UploadUtils.getInstance().uploadImage(bitmap, User.getUser().getCurrentUser(), friendId, Config.IMAGE_MSG, localId, listener,chatTpe);
    }

    public void updateMessage(String fri_ID, long LocalId, String msgId, String conversation, long timestamp, int status) {
        db.updateMsg(fri_ID, LocalId, msgId, conversation, timestamp, status);
    }

    private MessageBean imessage2Bean(IMessage message) {

        return new MessageBean(0, Config.STATUS_SENDING, message.getMsgType(), message.getContents(), TimeUtils.getTimestamp(), Config.TYPE_SEND, null, Long.parseLong(User.getUser().getCurrentUser()));
    }

    public void saveMessage(Message message,String chatTpe) {
        MessageBean newMsg = Msg2Bean(message);
        db.saveMsg(newMsg.getSenderId() + "", newMsg,chatTpe);
        lastMsgMap.put(newMsg.getSenderId() + "", newMsg.getServerId());
        add2ackList(message.getId());
    }

    private MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), Config.STATUS_SUCCESS, msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }

    public void ackAndFetch(FetchListener listener) {
        HttpUtils.postack(acklist, listener);
    }

    public void fetchNewMsg(FetchListener listener) {
        //HttpUtils.FetchNewMsg(User.getUser().getCurrentUser(), listener);
    }

    /**
     * 初始化Fetch
     */
    public void initAckAndFetch() {
        HttpUtils.postack(acklist, (list) -> {
            for (Message msg : list) {
                LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
            }
            LazyQueue.getInstance().TempDequeue();
        });
    }

    public void initFetch() {
//        System.out.println("fetchNewMsg IM");
//        HttpUtils.FetchNewMsg(User.getUser().getCurrentUser(), (list) -> {
//            for (Message msg : list) {
//                LazyQueue.getInstance().add2Temp(msg.getConversation(), msg);
//            }
//            LazyQueue.getInstance().TempDequeue();
//        });
    }

    public int saveReceiveMsg(Message message) {
        int result = db.saveReceiveMsg(message.getSenderId() + "", Msg2Bean(message), message.getConversation(), message.getGroupId(), message.getChatType());
        if (result == 0) {
            setLastMsg(message.getConversation(), message.getMsgId());
        }
        add2ackList(message.getId());
        return result;
    }

//    public void saveMessages(List<Message> list) {
//        List<MessageBean> list1 = new ArrayList<>();
//        for (Message message : list) {
//            list1.add(Msg2Bean(message));
//            System.out.println(message.getMsgId());
//        }
//        db.saveMsgs(list1);
//    }

    public void addGroup2Conversation(String groupId, String conversation) {
        db.add2Conversion(Long.parseLong(groupId), TimeUtils.getTimestamp(), "chat_" + CryptUtils.getMD5String(groupId), -1, conversation,"group");
    }
}

