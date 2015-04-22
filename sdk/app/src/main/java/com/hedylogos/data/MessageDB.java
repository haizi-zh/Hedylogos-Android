package com.hedylogos.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hedylogos.Utils.CryptUtils;
import com.hedylogos.bean.ConversationBean;
import com.hedylogos.bean.MessageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by q on 2015/4/21.
 */

public class MessageDB {
    public static final String MSG_DBNAME = "IM_SDK.db";
    public static final String DB_PATH = "/data/data/com.hedylogos/";
    public static final String TAG = "IMDB";
    String con_table_name;
    private SQLiteDatabase db;

    public MessageDB(String User_Id) {
        String path = CryptUtils.getMD5String(User_Id);
        con_table_name = "con_" + path;
        String DATABASE_PATH = DB_PATH + path;
        System.out.println("DATABASE_PATH:" + DATABASE_PATH);
        String databaseFilename = DATABASE_PATH + "/" + MSG_DBNAME;
        System.out.println("databaseFilename:" + databaseFilename);
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())
            dir.mkdir();
        db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);

    }

    public void saveMsg(String Friend_Id, MessageBean entity) {
        String table_name = "chat_" + CryptUtils.getMD5String(Friend_Id);
        db.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + " (LocalId INTEGER PRIMARY KEY AUTOINCREMENT,ServerId INTEGER,Status INTEGER," +
                "Type INTEGER, Message TEXT,CreateTime INTEGER, SendType INTEGER, Metadata TEXT," +
                "SenderId INTEGER)");
        ContentValues values = new ContentValues();
        values.put("ServerId", entity.getServerId());
        values.put("Status", entity.getStatus());
        values.put("Type", entity.getType());
        values.put("Message", entity.getMessage());
        values.put("CreateTime", entity.getCreateTime());
        values.put("SendType", entity.getSendType());
        values.put("Metadata", entity.getMetadata());
        values.put("SenderId", entity.getSenderId());
        db.insert(table_name, null, values);
        add2Conversion(Integer.parseInt(Friend_Id), entity.getCreateTime(), table_name);
//        db.execSQL(
//                "insert into "
//                        + table_name
//                        + " (ServerId,Status,Type,Message,CreateTime," +
//                        "SendType,Metadata," +
//                        "SenderId) values(?,?,?,?,?,?,?,?)",
//                new Object[]{entity.getServerId(), entity.getStatus(),
//                        entity.getType(), entity.getMessage(), entity.getCreateTime(),
//                        entity.getSendType(), entity.getMetadata(), entity.getSenderId()});
    }

    public List<MessageBean> getAllMsg(String Friend_Id, int pager) {
        String table_name = "chat_" + CryptUtils.getMD5String(Friend_Id);
        List<MessageBean> list = new LinkedList<MessageBean>();
        int num = 100 * (pager + 1);
        // 滚动到顶端自动加载数据
        db.execSQL("CREATE table IF NOT EXISTS "
                + table_name
                + " (LocalId INTEGER PRIMARY KEY AUTOINCREMENT,ServerId INTEGER,Status INTEGER," +
                "Type INTEGER, Message TEXT,CreateTime INTEGER, SendType INTEGER, Metadata TEXT," +
                "SenderId INTEGER)");
        Cursor c = db.rawQuery("SELECT * from " + table_name
                + " ORDER BY LocalId DESC LIMIT " + num, null);
        while (c.moveToNext()) {
            list.add(Curson2Message(c));
        }
        c.close();
        // Collections.reverse(list);// 前后反转一下消息记录
        return list;
    }
    public MessageBean Curson2Message(Cursor c) {
        int LocalId = c.getInt(c.getColumnIndex("LocalId"));
        int ServerId = c.getInt(c.getColumnIndex("ServerId"));
        int Status = c.getInt(c.getColumnIndex("Status"));
        int Type = c.getInt(c.getColumnIndex("Type"));
        String Message = c.getString(c.getColumnIndex("Message"));
        long CreateTime = c.getLong(c.getColumnIndex("CreateTime"));
        int SendType = c.getInt(c.getColumnIndex("SendType"));
        String Metadata = c.getString(c.getColumnIndex("Metadata"));
        int SenderId = c.getInt(c.getColumnIndex("SenderId"));
        MessageBean entity = new MessageBean(ServerId, Status, Type, Message, CreateTime, SendType, Metadata, SenderId);
        return entity;
    }
    public void add2Conversion(int Friend_Id, long lastTime, String hash) {
        db.execSQL("create index if not exists index_Friend_Id on "+con_table_name+"(Friend_Id)");
        db.execSQL("CREATE table IF NOT EXISTS "
                + con_table_name
                + " (Friend_Id INTEGER PRIMARY KEY,lastTime INTEGER,HASH TEXT)");
        ContentValues values = new ContentValues();
        values.put("Friend_Id", Friend_Id);
        values.put("lastTime", lastTime);
        values.put("HASH", hash);
        db.replace(con_table_name,null,values);
       // db.insert(con_table_name, null, values);
    }
    public List<ConversationBean> getConversationList() {
        List<ConversationBean> list = new ArrayList<ConversationBean>();
        Cursor c = db.rawQuery("SELECT * FROM " + con_table_name, null);
        while (c.moveToNext()) {
            long time = c.getLong(c.getColumnIndex("lastTime"));
            int friend_id = c.getInt(c.getColumnIndex("Friend_Id"));
            String table = c.getString(c.getColumnIndex("HASH"));
            Cursor cursor = db.rawQuery("SELECT * FROM " + table + " order by LocalId desc limit 1", null);
            cursor.moveToLast();
            String lastmessage = cursor.getString(4);
            System.out.println("lastmessage:"+lastmessage);
            list.add(new ConversationBean(friend_id, time, lastmessage));
            cursor.close();
        }
        c.close();
        return list;
    }

    public void test_Insert(){
        db.beginTransaction();
        for (int i=1;i<=100;i++){
            for (int j=1;j<=100;j++){
                saveMsg(i+"", new MessageBean("hhh" +j));
            }
        }
        db.endTransaction();
    }
}
