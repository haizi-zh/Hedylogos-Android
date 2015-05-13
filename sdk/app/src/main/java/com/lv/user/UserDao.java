package com.lv.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lv.Utils.CryptUtils;
import com.lv.data.MessageDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by q on 2015/5/11.
 */
public class UserDao {
    private SQLiteDatabase db;
    private String User_table_name;
    private String databaseFilename;
    private  static UserDao instance;
    private UserDao(String User_Id) {
        String path = CryptUtils.getMD5String(User_Id);
        User_table_name = "fri_" + path;
    }

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao(User.getUser().getCurrentUser());
        }
        return instance;
    }
    private SQLiteDatabase getDB(){
       return MessageDB.getInstance().getDB();
    }
    private void closeDB(){
        MessageDB.getInstance().closeDB();
    }
//    {
//        "id": "554d82e546e0fb0204ed12a8",
//            "groupId": 900056,
//            "name": "第33个群",
//            "creator": 100002,
//            "groupType": "common",
//            "isPublic": true,
//            "conversation": "554d82e546e0fb0204ed12a8" //群组同时维护的会话的ID
//    }

    public void add2User(String GroupId){
        db=getDB();
        Cursor c=db.rawQuery("select * from " + User_table_name + " where UserId=" + GroupId, null);
        ContentValues values=new ContentValues();
        values.put("groupId",GroupId);

        if (c.getCount()==0){
            db.insert(User_table_name,null,values);
        }
        c.close();
        closeDB();
    }
//    "name": "尼泊尔旅游",
//            "groupType": "common",
//            "isPublic": true,
//            "avatar": "http://...",
//            "participants":[ 100000,100001 ]
    public synchronized long addGroup2Friend(String name ,String groupType,boolean isPbulic,String avatar,List<Long> participants) {
        db=getDB();
        db.execSQL("CREATE table IF NOT EXISTS "
                + User_table_name
                + " (_Id INTEGER PRIMARY KEY AUTOINCREMENT ,Friend_Id INTEGER ,NickName TEXT, Avatar TEXT, AvatarSmall TEXT," +
                " ShortPY TEXT, FullPY TEXT, Singature TEXT,Memo TEXT,Sex INTEGER  ,Type INTEGER,conversation TEXT,ext TEXT)");
        db.execSQL("create index if not exists index_Fri_Friend_Id on " + User_table_name + "(Friend_Id)");
        System.out.println("fri_table_name:" + User_table_name);
        JSONObject object=new JSONObject();
        try {
            //object.put("participants",participants.toString());
            object.put("groupType",groupType);
            object.put("isPbulic",isPbulic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put("NickName", name);
        values.put("Type", 8);
        values.put("Avatar", avatar);
        values.put("ext", object.toString());
        long row=db.insert(User_table_name, null, values);
        closeDB();
        return row;
    }

    public synchronized void updateGroup(long row,String groupId,String conversation){
        db=getDB();
        ContentValues values = new ContentValues();
        values.put("Friend_Id", groupId);
        values.put("conversation",conversation);
        int num= db.update(User_table_name, values, "_Id=?", new String[]{row + ""});
        closeDB();
    }
}
