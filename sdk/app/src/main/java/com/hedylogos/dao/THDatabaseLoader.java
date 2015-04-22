package com.hedylogos.dao;

import android.content.Context;

import com.hedylogos.data.THDevOpenHelper;

/**
 * Created by q on 2015/4/16.
 */
public class THDatabaseLoader {
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    public static DaoMaster getDaoMaster(Context c) {

        if (daoMaster == null) {
            THDevOpenHelper helper = new THDevOpenHelper(c, "msgdb.db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
