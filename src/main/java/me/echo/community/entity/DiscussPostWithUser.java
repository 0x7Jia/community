package me.echo.community.entity;

public class DiscussPostWithUser extends DiscussPost{
    private String userName;
    private String email;
    private Integer status;
    private String headerUrl;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }


    @Override
    public String toString() {
        return super.toString()+"DiscussPostWithUser{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", headerUrl='" + headerUrl + '\'' +
                '}';
    }
}
