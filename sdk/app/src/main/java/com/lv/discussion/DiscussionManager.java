package com.lv.discussion;

import com.lv.group.CreateSuccessListener;
import com.lv.user.HttpManager;
import com.lv.user.UserDao;

import java.util.List;

/**
 * Created by q on 2015/5/18.
 */
public class DiscussionManager {
    private static DiscussionManager discussionManager;

    private DiscussionManager() {
    }

    public static DiscussionManager getDiscussionManager() {
        if (discussionManager == null) {
            discussionManager = new DiscussionManager();
        }
        return discussionManager;
    }

    public void createDiscussion(String groupName, String groupType, boolean isPublic, List<Long> groupMember, CreateSuccessListener listener) {
        long row = UserDao.getInstance().addGroup2User(groupName, groupType, isPublic, null, groupMember);
        HttpManager.createGroup(groupName, groupType, isPublic, null, groupMember, row, listener);
    }

    public void addMembers(String groupId, List<Long> members, boolean isPublic) {
        HttpManager.addMembers(groupId, members, isPublic);

    }

    public void joinDiscussion(String groupId, String message) {

    }

    public void removeMembers(String groupId, List<Long> members, boolean isPublic) {
        HttpManager.removeMembers(groupId, members, isPublic);
    }

    public void silenceMembers(String groupId, List<Long> members, boolean isPublic) {
        HttpManager.silenceMembers(groupId, members, isPublic);
    }

    public void quitDiscussion(String groupId) {

    }

    public void getDiscussionInformation(String groupId) {
        HttpManager.getGroupInformation(groupId);
    }

    public void getDiscussionMembers(String groupId) {
        HttpManager.getGroupMembers(groupId);
    }

    public void getUserDiscussionInfo(String userId) {
        HttpManager.getUserGroupInfo(userId);
    }

    public void searchDiscussion(String tag, String value) {
        HttpManager.searchGroup("groupId", "900052");
    }
}


