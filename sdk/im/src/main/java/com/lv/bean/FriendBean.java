package com.lv.bean;

/**
 * Created by q on 2015/4/21.
 */
public class FriendBean {
    private int UserId;
    private String NickName; //用户昵称
    private String Avatar;      // 3.Avatar:大头像 url
    private String AvatarSmall;// 4.AvatarSmall：小头像
    private String ShortPY;//5.ShortPY, FullPY ：拼音的简拼和全拼
    private String FullPY;//
    private String Singature;//  6.Signature: 签名
    private String Memo;//备注
    private int Sex;// 7.Sex: 性别   1：male   2：female
    private int Type;// 8.Type：好友类型，32位整形，先使用后16位，其余16位留以备用， 其中1为是，0为否

    public FriendBean(int userId) {
        UserId = userId;
    }

    public FriendBean(int userId, int type, int sex, String memo, String singature, String fullPY, String shortPY, String avatarSmall, String avatar, String nickName) {
        UserId = userId;
        Type = type;
        Sex = sex;
        Memo = memo;
        Singature = singature;
        FullPY = fullPY;
        ShortPY = shortPY;
        AvatarSmall = avatarSmall;
        Avatar = avatar;
        NickName = nickName;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getAvatarSmall() {
        return AvatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        AvatarSmall = avatarSmall;
    }

    public String getShortPY() {
        return ShortPY;
    }

    public void setShortPY(String shortPY) {
        ShortPY = shortPY;
    }

    public String getFullPY() {
        return FullPY;
    }

    public void setFullPY(String fullPY) {
        FullPY = fullPY;
    }

    public String getSingature() {
        return Singature;
    }

    public void setSingature(String singature) {
        Singature = singature;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }
}
