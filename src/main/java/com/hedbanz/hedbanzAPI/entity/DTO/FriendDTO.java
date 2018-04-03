package com.hedbanz.hedbanzAPI.entity.DTO;

public class FriendDTO {

    private Long id;
    private String login;
    private String imagePath;
    private boolean isAccepted;

    public FriendDTO(){};

    public FriendDTO(long id, String login, String imagePath){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
    }

    public FriendDTO(long id, String login, String imagePath, int isAccepted){
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

    public boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean isAccepted) {
        isAccepted = isAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendDTO friendDTO = (FriendDTO) o;

        if (!id.equals(friendDTO.id)) return false;
        if (!login.equals(friendDTO.login)) return false;
        return imagePath.equals(friendDTO.imagePath);
    }
}
