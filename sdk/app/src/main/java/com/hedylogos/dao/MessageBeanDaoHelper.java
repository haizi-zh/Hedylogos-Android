package com.hedylogos.dao;

import android.content.Context;
import android.text.TextUtils;

import com.hedylogos.bean.Message;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by q on 2015/4/16.
 */
public class MessageBeanDaoHelper implements THDaoHelperInterface {
    private static MessageBeanDaoHelper instance;
    private MessageDao userBeanDao;


    private MessageBeanDaoHelper(Context context) {
        try {
            userBeanDao =  THDatabaseLoader.getDaoSession(context).getMessageDao();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MessageBeanDaoHelper getInstance(Context context) {
        if(instance == null) {
            instance = new MessageBeanDaoHelper(context);
        }

        return instance;
    }

    @Override
    public <T> void addData(T bean) {
        if(userBeanDao != null && bean != null) {
            userBeanDao.insertOrReplace((Message) bean);
        }
    }
    public boolean isSaved(int Id)
    {
        QueryBuilder<Message> qb = userBeanDao.queryBuilder();
        qb.where(MessageDao.Properties.Id.eq(Id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0;
    }
    @Override
    public void deleteData(String id) {
        if(userBeanDao != null && !TextUtils.isEmpty(id)) {
            userBeanDao.deleteByKey(id);
        }
    }

    @Override
    public Message getDataById(String id) {
        if(userBeanDao != null && !TextUtils.isEmpty(id)) {
            return userBeanDao.load(id);
        }
        return null;
    }

    @Override
    public List getAllData() {
        if(userBeanDao != null) {
            return userBeanDao.loadAll();
        }
        return null;
    }

    @Override
    public boolean hasKey(String id) {
        if(userBeanDao == null || TextUtils.isEmpty(id)) {
            return false;
        }

        QueryBuilder<Message> qb = userBeanDao.queryBuilder();
        qb.where(MessageDao.Properties.Id.eq(id));
        long count = qb.buildCount().count();
        return count > 0;
    }

    @Override
    public long getTotalCount() {
        if(userBeanDao == null) {
            return 0;
        }

        QueryBuilder<Message> qb = userBeanDao.queryBuilder();
        return qb.buildCount().count();
    }

    @Override
    public void deleteAll() {
        if(userBeanDao != null) {
            userBeanDao.deleteAll();
        }
    }
    public void testQueryBy() {
        List joes = userBeanDao.queryBuilder()
                .where(MessageDao.Properties.Content.eq("Joe"))
                .orderAsc(MessageDao.Properties.Content)
                .list();
        QueryBuilder<Message> qb = userBeanDao.queryBuilder();
        qb.where(qb.or(MessageDao.Properties.Content.gt(10698.85),
                qb.and(MessageDao.Properties.Content.eq("id"),
                        MessageDao.Properties.Content.eq("xx"))));
        qb.orderAsc(MessageDao.Properties.Id);// 排序依据
        qb.list();
    }
}
