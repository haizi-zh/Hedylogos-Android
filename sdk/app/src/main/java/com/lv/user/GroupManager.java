package com.lv.user;

import java.util.List;

/**
 * Created by q on 2015/5/11.
 */
public class GroupManager {
    private static GroupManager groupManager;

    private GroupManager() {
    }

    public static GroupManager getGroupManager() {
        if (groupManager == null) {
            groupManager = new GroupManager();
        }
        return groupManager;
    }

    public void createGroup(String groupName, String groupType, boolean isPublic, List<Long> groupMember,CreateSuccessListener listener) {
       long row= UserDao.getInstance().addGroup2Friend(groupName,groupType,isPublic,null,groupMember);
       HttpManager.createGroup(groupName, groupType, isPublic, null, groupMember, row,listener);
    }


    public void addMembers(String groupId, List<Long> members, boolean isPublic) {
        HttpManager.addMembers(groupId, members, isPublic);

    }

    public void joinGroup(String groupId, String message) {

    }

    public void removeMembers(String groupId, List<Long> members, boolean isPublic) {
        HttpManager.removeMembers(groupId, members, isPublic);
    }

    public void silenceMembers(String groupId, List<Long> members, boolean isPublic) {
        HttpManager.silenceMembers(groupId, members, isPublic);
    }

    public void quitGroup(String groupId) {
    }

    public void getGroupInformation(String groupId) {
        HttpManager.getGroupInformation(groupId);
    }

    public void getGroupMembers(String groupId) {
        HttpManager.getGroupMembers(groupId);
    }
    public void getUserGroupInfo(String userId){
        HttpManager.getUserGroupInfo(userId);
    }
    public void searchGroup(String tag,String value){
        HttpManager.searchGroup("groupId","900052");
    }
}
