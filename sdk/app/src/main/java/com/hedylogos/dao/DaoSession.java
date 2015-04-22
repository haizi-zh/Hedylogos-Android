package com.hedylogos.dao;

import android.database.sqlite.SQLiteDatabase;

import com.hedylogos.bean.Message;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;




// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig messageDaoConfig;

    private final MessageDao messageDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        messageDaoConfig = daoConfigMap.get(MessageDao.class).clone();
        messageDaoConfig.initIdentityScope(type);

        messageDao = new MessageDao(messageDaoConfig, this);

        registerDao(Message.class, messageDao);
    }
    
    public void clear() {
        messageDaoConfig.getIdentityScope().clear();
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

}
