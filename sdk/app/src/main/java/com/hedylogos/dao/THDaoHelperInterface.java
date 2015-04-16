package com.hedylogos.dao;

import java.util.List;

/**
 * Created by q on 2015/4/16.
 */
public interface THDaoHelperInterface {

    public <T> void addData(T t);
    public void deleteData(String id);
    public <T> T getDataById(String id);
    public List getAllData();
    public boolean hasKey(String id);
    public long getTotalCount();
    public void deleteAll();
}
