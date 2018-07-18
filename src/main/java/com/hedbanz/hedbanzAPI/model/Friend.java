package com.hedbanz.hedbanzAPI.model;

public class Friend {
    private Long id;
    private String login;
    private String imagePath;
    private Boolean isPending;
    private Boolean isAccepted;
    private Boolean isInRoom;
    private Boolean isInvited;

    public Friend(){}

    public Friend(Long id, String login, String imagePath){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
    }

    public Friend(Long id, String login, String imagePath, int isAccepted, int isPending){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
        this.isAccepted = (isAccepted != 0);
        this.isPending = (isPending != 0);
    }

    public Friend(Long id, String login, String imagePath, int isPending, int isAccepted, int isInRoom, int isInvited) {
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
        this.isAccepted = (isAccepted != 0);
        this.isPending = (isPending != 0);
        this.isInRoom = isInRoom != 0;
        this.isInvited = isInvited != 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public Boolean getIsPending() {
        return isPending;
    }

    public void setIsPending(Boolean pending) {
        isPending = pending;
    }

    public Boolean getIsInRoom() {
        return isInRoom;
    }

    public Boolean getIsInvited() {
        return isInvited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Friend friend = (Friend) o;

        if (id != null ? !id.equals(friend.id) : friend.id != null) return false;
        if (login != null ? !login.equals(friend.login) : friend.login != null) return false;
        return imagePath != null ? imagePath.equals(friend.imagePath) : friend.imagePath == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
        return result;
    }
}
