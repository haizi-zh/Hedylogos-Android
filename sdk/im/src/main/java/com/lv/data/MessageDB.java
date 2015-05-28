package com.lv.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lv.Utils.Config;
import com.lv.Utils.CryptUtils;
import com.lv.bean.Conversation;
import com.lv.bean.ConversationBean;
import com.lv.bean.MessageBean;
import com.lv.im.IMClient;
import com.lv.user.User;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by q on 2015/4/21.
 */

public class MessageDB {
    private String con_table_name;
    private String fri_table_name;
    private SQLiteDatabase db;
    private static MessageDB instance;
    private String databaseFilename;
    private static final int CONVERSATION_INDEX_lastTime = 1;
    private static final int CONVERSATION_INDEX_Friend_Id = 0;
    private static final int CONVERSATION_INDEX_HASH = 2;
    private static final int CONVERSATION_INDEX_last_rec_msgId = 3;
    private static final int CONVERSATION_INDEX_isRead = 5;

    private static final int MESSAGE_INDEX_LocalId = 0;
    private static final int MESSAGE_INDEX_ServerId = 1;
    private static final int MESSAGE_INDEX_Status = 2;
    private static final int MESSAGE_INDEX_Type = 3;
    private static final int MESSAGE_INDEX_Message = 4;
    private static final int MESSAGE_INDEX_CreateTime = 5;
    private static final int MESSAGE_INDEX_SendType = 6;
    private static final int MESSAGE_INDEX_Metadata = 7;
    private static final int MESSAGE_INDEX_SenderId = 8;

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mdb;

    ExecutorService dbThread = Executors.newFixedThreadPool(1);

    private MessageDB(String User_Id) {
        String path = CryptUtils.getMD5String(User_Id);
        con_table_name = "con_" + path;
        fri_table_name = "fri_" + path;
        String DATABASE_PATH = Config.DB_PATH + path;
        databaseFilename = DATABASE_PATH + "/" + Config.MSG_DBNAME;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())
            dir.mkdir();
        db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
    }

    public static MessageDB getInstance() {
        if (instance == null) {
            instance = new MessageDB(User.getUser().getCurrentUser());
        }
        return instance;
    }

    public synchronized SQLiteDatabase getDB() {
        if (mOpenCounter.incrementAndGet() == 1) {
            db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READWRITE);
        }
        System.out.println("开启数量" + mOpenCounter.get());
        return db;
    }

    public synchronized void closeDB() {
        if (mOpenCounter.decrementAndGet() == 0) {
            db.close();
        }
        mdb=null;
        System.out.println("关闭数量" + mOpenCounter.get());
    }
    public void init(){
        mdb=getDB();
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + con_table_name
                + " (Friend_Id INTEGER PRIMARY KEY,lastTime INTEGER,HASH TEXT,last_rec_msgId INTEGER, IsRead INTEGER,conversation TEXT,chatType TEXT)");
        mdb.execSQL("create index if not exists index_Con_Friend_Id on " + con_table_name + "(Friend_Id)");

    }
    public synchronized long saveMsg(final String Friend_Id, final MessageBean entity,String chatType) {

        String table_name = "chat_" + CryptUtils.getMD5String(Friend_Id);
        mdb = getDB();
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + " (LocalId INTEGER PRIMARY KEY AUTOINCREMENT,ServerId INTEGER,Status INTEGER," +
                "Type INTEGER, Message TEXT,CreateTime INTEGER, SendType INTEGER, Metadata TEXT," +
                "SenderId INTEGER)");
        //db.execSQL("create index if not exists index_Msg_Friend_Id on " + fri_table_name + "(Friend_Id)");
        ContentValues values = new ContentValues();
        values.put("ServerId", entity.getServerId());
        values.put("Status", entity.getStatus());
        values.put("Type", entity.getType());
        values.put("Message", entity.getMessage());
        values.put("CreateTime", entity.getCreateTime());
        values.put("SendType", entity.getSendType());
        values.put("Metadata", entity.getMetadata());
        values.put("SenderId", entity.getSenderId());
        long localid = mdb.insert(table_name, null, values);
        add2Conversion(Integer.parseInt(Friend_Id), entity.getCreateTime(), table_name, entity.getServerId(), null,chatType);
        closeDB();
        return localid;
    }

    public synchronized int saveReceiveMsg(String Friend_Id, MessageBean entity, String conversation, long groupId, String chatType) {
        mdb = getDB();
        String table_name = null;
        String chater = null;
        System.out.println("Friend_Id " + Friend_Id + " conversation " + conversation);
        /**
         * 单聊
         */
        if ("single".equals(chatType)) {
            table_name = "chat_" + CryptUtils.getMD5String(entity.getSenderId() + "");
            chater = entity.getSenderId() + "";
        }
        /**
         * 群聊
         */
        if ("group".equals(chatType)) {
            table_name = "chat_" + CryptUtils.getMD5String(groupId + "");
            chater = groupId + "";
        }

        if (Config.isDebug) {
            Log.i(Config.TAG, "table_name " + table_name);
        }
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + " (LocalId INTEGER PRIMARY KEY AUTOINCREMENT,ServerId INTEGER,Status INTEGER," +
                "Type INTEGER, Message TEXT,CreateTime INTEGER, SendType INTEGER, Metadata TEXT," +
                "SenderId INTEGER)");
        // db.execSQL("create UNIQUE index if not exists index_Msg_Id on " + table_name + "(ServerId)");

        Cursor cursor = mdb.rawQuery("select * from " + table_name + " where ServerId=?", new String[]{entity.getServerId() + ""});
        int count = cursor.getCount();
        if (count > 0) return 1;
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("ServerId", entity.getServerId());
        values.put("Status", entity.getStatus());
        values.put("Type", entity.getType());
        values.put("Message", entity.getMessage());
        values.put("CreateTime", entity.getCreateTime());
        values.put("SendType", entity.getSendType());
        values.put("Metadata", entity.getMetadata());
        values.put("SenderId", entity.getSenderId());
        mdb.insert(table_name, null, values);
        IMClient.getInstance().setLastMsg(conversation, entity.getServerId());
        add2Conversion(Long.parseLong(chater), entity.getCreateTime(), table_name, entity.getServerId(), conversation,chatType);
        closeDB();
        IMClient.getInstance().add2ConversationList(new ConversationBean(Integer.parseInt(chater), entity.getCreateTime(), null, entity.getServerId(), 0, conversation, entity.getMessage(),chatType));
        return 0;
    }

    public List<MessageBean> getAllMsg(String Friend_Id, int pager) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(Friend_Id);
        List<MessageBean> list = new LinkedList<MessageBean>();
        // 滚动到顶端自动加载数据
        int from = pager * 100;
        int to = (pager + 1) * 100;
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + " (LocalId INTEGER PRIMARY KEY AUTOINCREMENT,ServerId INTEGER,Status INTEGER," +
                "Type INTEGER, Message TEXT,CreateTime INTEGER, SendType INTEGER, Metadata TEXT," +
                "SenderId INTEGER)");
        Cursor c = mdb.rawQuery("SELECT * from " + table_name
                + " ORDER BY LocalId DESC LIMIT " + from + "," + to, null);
        if (c.getCount() == 0)
            return list;
        c.moveToLast();
        if (c.getCount() > 0) {
            list.add(Curson2Message(c));
        }
        while (c.moveToPrevious()) {
            list.add(Curson2Message(c));
        }
        // updateReadStatus(Friend_Id);
        c.close();
        closeDB();
        return list;
    }

   public synchronized void updateReadStatus(String conversation, int num) {
        mdb = getDB();
        ContentValues values = new ContentValues();
        if (num == 0) {
            values.put("IsRead", num);
        }
        if (num==1){
            Cursor c = mdb.rawQuery("select IsRead from " + con_table_name + " where conversation='" + conversation + "'", null);
            c.moveToLast();
               int  n = c.getInt(0);
                if (Config.isDebug){
                    Log.i(Config.TAG,"未读数量： " + n);
                }
                values.put("IsRead", n+1);
            c.close();
       }
        mdb.update(con_table_name, values, " conversation=?", new String[]{conversation});
        closeDB();
    }

    public MessageBean Curson2Message(Cursor c) {
        int LocalId = c.getInt(MESSAGE_INDEX_LocalId);
        int ServerId = c.getInt(MESSAGE_INDEX_ServerId);
        int Status = c.getInt(MESSAGE_INDEX_Status);
        int Type = c.getInt(MESSAGE_INDEX_Type);
        String Message = c.getString(MESSAGE_INDEX_Message);
        long CreateTime = c.getLong(MESSAGE_INDEX_CreateTime);
        int SendType = c.getInt(MESSAGE_INDEX_SendType);
        String Metadata = c.getString(MESSAGE_INDEX_Metadata);
        int SenderId = c.getInt(MESSAGE_INDEX_SenderId);
        return new MessageBean(ServerId, Status, Type, Message, CreateTime, SendType, Metadata, SenderId);
    }

    public synchronized void add2Conversion(long Friend_Id, long lastTime, String hash, int last_rec_msgId, String conversation,String chatType) {
        mdb = getDB();
        mdb.execSQL("CREATE table IF NOT EXISTS "
                + hash
                + " (LocalId INTEGER PRIMARY KEY AUTOINCREMENT,ServerId INTEGER,Status INTEGER," +
                "Type INTEGER, Message TEXT,CreateTime INTEGER, SendType INTEGER, Metadata TEXT," +
                "SenderId INTEGER)");
        ContentValues values = new ContentValues();
        values.put("Friend_Id", Friend_Id);
        values.put("lastTime", lastTime);
        values.put("HASH", hash);
        values.put("last_rec_msgId", last_rec_msgId);
        if (conversation != null) {
            values.put("conversation", conversation);
        }
        values.put("chatType", chatType);
        Cursor cursor = mdb.rawQuery("select Friend_Id from " + con_table_name + " where Friend_Id=" + Friend_Id + "", null);
        if (cursor.getCount() == 0) {
            mdb.insert(con_table_name, null, values);
        } else mdb.update(con_table_name, values, "Friend_Id=?", new String[]{Friend_Id + ""});
        cursor.close();
        closeDB();
        //IMClient.getInstance().addToConversationList(new Conversation());
      //  new ConversationBean(Friend_Id, lastTime, null, lastmsgId, isRead, conversation, lastMessage,chatType)
    }

    public List<ConversationBean> getConversationList() {
        mdb = getDB();
        List<ConversationBean> list = new ArrayList<ConversationBean>();
        Cursor c = mdb.rawQuery("SELECT * FROM " + con_table_name, null);
        while (c.moveToNext()) {
            long time = c.getLong(CONVERSATION_INDEX_lastTime);
            int friend_id = c.getInt(CONVERSATION_INDEX_Friend_Id);
            String table = c.getString(CONVERSATION_INDEX_HASH);
            int lastmsgId = c.getInt(CONVERSATION_INDEX_last_rec_msgId);
            int isRead = c.getInt(c.getColumnIndex("IsRead"));
            String conversation = c.getString(c.getColumnIndex("conversation"));
            String chatType = c.getString(c.getColumnIndex("chatType"));
            if (conversation == null) conversation = "0";
            IMClient.getInstance().setLastMsg(conversation, lastmsgId);
            Cursor cursor = mdb.rawQuery("SELECT Message FROM " + table + " order by ServerId desc limit 1", null);
            String lastMessage=null;
            if (cursor.getCount()>0) {
                cursor.moveToLast();
                lastMessage = cursor.getString(0);
            }
            list.add(new ConversationBean(friend_id, time, table, lastmsgId, isRead, conversation, lastMessage,chatType));
           // new Conversation(lastMessage,time,isRead,friend_id,0,conversation,chatType);
            //IMClient.getInstance().add2ConversationList(new ConversationBean(friend_id, time, table, lastmsgId, isRead, conversation, lastMessage,chatType));
            cursor.close();
        }
        c.close();
        closeDB();
        return list;
    }
    public synchronized void deleteConversation(String friendId){
        mdb=getDB();
        mdb.delete(con_table_name,"Friend_Id=?",new String[]{friendId});
        closeDB();
    }

    public synchronized void updateMsg(String fri_ID, long LocalId, String msgId, String conversation, long timestamp, int status) {
        mdb = getDB();
        String table_name = "chat_" + CryptUtils.getMD5String(fri_ID);
        ContentValues values = new ContentValues();
        values.put("ServerId", msgId);
        //values.put("conversation",conversation);
        values.put("CreateTime", timestamp);
        values.put("Status", status);
        int num = mdb.update(table_name, values, "LocalId=?", new String[]{LocalId + ""});
        updateConversation(fri_ID, conversation, Integer.parseInt(msgId));
        closeDB();
    }

    public synchronized void updateConversation(String fri_ID, String conversation, int last_msgId) {
        mdb = getDB();
        ContentValues values = new ContentValues();
        values.put("conversation", conversation);
        values.put("last_rec_msgId", last_msgId);
        mdb.update(con_table_name, values, "Friend_Id=?", new String[]{fri_ID});
        closeDB();
    }

//    public void saveMsgs(List<MessageBean> list) {
//        mdb = getDB();
//        mdb.beginTransaction();
//        for (MessageBean msg : list) {
//            saveMsg(msg.getSenderId() + "", msg);
//        }
//        mdb.endTransaction();
//        closeDB();
//    }

}
