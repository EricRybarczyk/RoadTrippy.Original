package ericrybarczyk.me.roadtrippy.dto;

public class User {

    private String userId;
    private String userName;
    private String emailAddress;
    private String createDate;
    private String editDate;

    public User() {
    }

    public User(String userId, String userName, String emailAddress, String createDate, String editDate) {
        this.userId = userId;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.createDate = createDate;
        this.editDate = editDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getEditDate() {
        return editDate;
    }

    public void setEditDate(String editDate) {
        this.editDate = editDate;
    }
}
