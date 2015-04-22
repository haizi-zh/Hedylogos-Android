package com.hedylogos.dao;

import java.util.List;

/**
 * Created by q on 2015/4/16.
 */
public interface THDaoHelperInterface {

    public <T> void addData(T t);

    public void deleteData(long msgId);

    public <T> T getDataById(long msgId);

    public List getAllData();

    public boolean hasKey(long msgId);

    public long getTotalCount();

    public void deleteAll();
}
