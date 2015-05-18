package com.lv.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lv.Utils.Config;
import com.lv.Utils.CryptUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by q on 2015/5/11.
 */
public class UserDao {
    private SQLiteDatabase db;
    private String User_table_name;
    private String databaseFilename;
    private static UserDao instance;
    private AtomicInteger counter = new AtomicInteger();

    private UserDao(String User_Id) {
        String path = CryptUtils.getMD5String(User_Id);
        User_table_name = "user_" + path;
        String DATABASE_PATH = Config.DB_PATH + path;
        databaseFilename = DATABASE_PATH + "/" + Config.USER_DBNAME;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())
            dir.mkdir();
        db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);

    }

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao(User.getUser().getCurrentUser());
        }
        return instance;
    }

    private SQLiteDatabase getDB() {
        if (counter.incrementAndGet() == 1) {
            db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return db;
    }

    private void closeDB() {
        if (counter.decrementAndGet() == 0) {
            db.close();
        }
    }

    public void add2User(String UserId) {
        db = getDB();
        db.execSQL("CREATE table IF NOT EXISTS "
                + User_table_name
                + " (_Id INTEGER PRIMARY KEY AUTOINCREMENT ,User_Id INTEGER ,NickName TEXT, Avatar TEXT, AvatarSmall TEXT," +
                " ShortPY TEXT, FullPY TEXT, Singature TEXT,Memo TEXT,Sex INTEGER  ,Type INTEGER,conversation TEXT,ext TEXT)");
        Cursor c = db.rawQuery("select * from " + User_table_name + " where UserId=" + UserId, null);
        ContentValues values = new ContentValues();
        values.put("groupId", UserId);

        if (c.getCount() == 0) {
            db.insert(User_table_name, null, values);
        }
        c.close();
        closeDB();
    }

    public synchronized long addGroup2User(String name, String groupType, boolean isPublic, String avatar, List<Long> participants) {
        db = getDB();
        db.execSQL("CREATE table IF NOT EXISTS "
                + User_table_name
                + " (_Id INTEGER PRIMARY KEY AUTOINCREMENT ,User_Id INTEGER ,NickName TEXT, Avatar TEXT, AvatarSmall TEXT," +
                " ShortPY TEXT, FullPY TEXT, Singature TEXT,Memo TEXT,Sex INTEGER  ,Type INTEGER,conversation TEXT,ext TEXT)");
        //db.execSQL("create index if not exists index_User_Id on " + User_table_name + "(User_Id)");
        System.out.println("user_table_name:" + User_table_name);
        JSONObject object = new JSONObject();
        try {
            //object.put("participants",participants.toString());
            object.put("groupType", groupType);
            object.put("isPublic", isPublic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put("NickName", name);
        values.put("Type", 8);
        values.put("Avatar", avatar);
        values.put("ext", object.toString());
        long row = db.insert(User_table_name, null, values);
        closeDB();
        return row;
    }

    public synchronized void updateGroup(long row, String groupId, String conversation) {
        db = getDB();
        ContentValues values = new ContentValues();
        values.put("User_Id", groupId);
        values.put("conversation", conversation);
        int num = db.update(User_table_name, values, "_Id=?", new String[]{row + ""});
        closeDB();
    }
}
