package com.hedbanz.hedbanzAPI.entity.DTO;

public class PlayerDTO {
    private Long id;
    private String login;
    private String imagePath;

    public PlayerDTO(){}

    public PlayerDTO(Long id, String login, String imagePath){
        this.id = id;
        this.login = login;
        this.imagePath = imagePath;
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
}
