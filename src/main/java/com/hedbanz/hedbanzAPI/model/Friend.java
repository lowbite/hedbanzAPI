package com.hedbanz.hedbanzAPI.model;

public class Friend {
    private Long id;
    private String login;
    private Integer iconId;
    private Boolean isPending;
    private Boolean isAccepted;
    private Boolean isInRoom;
    private Boolean isInvited;

    public Friend(){}

    public Friend(Long id, String login, Integer iconId){
        this.id = id;
        this.login = login;
        this.iconId = iconId;
    }

    public Friend(Long id, String login, Integer iconId, int isAccepted, int isPending){
        this.id = id;
        this.login = login;
        this.iconId = iconId;
        this.isAccepted = (isAccepted != 0);
        this.isPending = (isPending != 0);
    }

    public Friend(Long id, String login, Integer iconId, int isPending, int isAccepted, int isInRoom, int isInvited) {
        this.id = id;
        this.login = login;
        this.iconId = iconId;
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

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
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
        return iconId != null ? iconId.equals(friend.iconId) : friend.iconId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (iconId != null ? iconId.hashCode() : 0);
        return result;
    }
}
