package com.hedbanz.hedbanzAPI.transfer;


public class GameOverDto {
    private Boolean isGameOver;

    public GameOverDto(Boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public Boolean getIsGameOver() {
        return isGameOver;
    }

    public void setIsGameOver(Boolean gameOver) {
        isGameOver = gameOver;
    }
}
