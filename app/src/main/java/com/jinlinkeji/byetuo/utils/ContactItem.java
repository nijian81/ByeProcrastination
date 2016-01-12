package com.jinlinkeji.byetuo.utils;

/**
 * Created by nijian on 2015/8/31.
 */
public class ContactItem {

    private String userName;
    private String content;

    public ContactItem(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
