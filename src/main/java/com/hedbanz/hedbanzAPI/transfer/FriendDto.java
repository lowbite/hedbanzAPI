package com.hedbanz.hedbanzAPI.transfer;

public class FriendDto {

    private Long id;
    private String login;
    private String imagePath;
    private Boolean isAccepted;

    public FriendDto(){};

    public FriendDto(Long id, String login, String imagePath){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
    }

    public FriendDto(Long id, String login, String imagePath, int isAccepted){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
        this.isAccepted = (isAccepted != 0);
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

    public void setIsAccepted(Boolean isAccepted) {
        isAccepted = isAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendDto friendDto = (FriendDto) o;

        if (!id.equals(friendDto.id)) return false;
        if (!login.equals(friendDto.login)) return false;
        return imagePath.equals(friendDto.imagePath);
    }
}
