package com.hedbanz.hedbanzAPI.transfer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.RoomDeserializer;

import java.util.List;


//@JsonDeserialize(using = RoomDeserializer.class)
public class RoomDto {
    private Long id;
    private String name;
    private String password;
    private Integer maxPlayers;
    private List<PlayerDto> players;
    private Integer currentPlayersNumber;
    private Boolean isPrivate;
    private Long userId;
    private Long stickerId;
    private Long iconId;
    private Integer gameStatus;

    public RoomDto(){

    }

    public RoomDto(Long id, String name, Integer maxPlayers, Integer currentPlayersNumber, Boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayersNumber = currentPlayersNumber;
        this.isPrivate = isPrivate;
    }

    private RoomDto(Long id, String name, String password, Integer maxPlayers, List<PlayerDto> players,
                    Integer currentPlayersNumber, Boolean isPrivate, Long userId, Long stickerId, Long iconId,
                    Integer gameStatus) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.players = players;
        this.currentPlayersNumber = currentPlayersNumber;
        this.isPrivate = isPrivate;
        this.userId = userId;
        this.stickerId = stickerId;
        this.iconId = iconId;
        this.gameStatus = gameStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    public Integer getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }

    public void setCurrentPlayersNumber(Integer currentPlayersNumber) {
        this.currentPlayersNumber = currentPlayersNumber;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStickerId() {
        return stickerId;
    }

    public void setStickerId(Long stickerId) {
        this.stickerId = stickerId;
    }

    public Long getIconId() {
        return iconId;
    }

    public void setIconId(Long iconId) {
        this.iconId = iconId;
    }

    public Integer getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
    }

    public static class Builder {
        private Long id;
        private String name;
        private Integer maxPlayers;
        private Integer currentPlayersNumber;
        private Boolean isPrivate;
        private String password;
        private List<PlayerDto> players;
        private Long userId;
        private Long stickerId;
        private Long iconId;
        private Integer gameStatus;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setMaxPlayers(Integer maxPlayers) {
            this.maxPlayers = maxPlayers;
            return this;
        }

        public Builder setCurrentPlayersNumber(Integer currentPlayersNumber) {
            this.currentPlayersNumber = currentPlayersNumber;
            return this;
        }

        public Builder setIsPrivate(Boolean isPrivate) {
            this.isPrivate = isPrivate;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setPlayers(List<PlayerDto> players) {
            this.players = players;
            return this;
        }

        public Builder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setStickerId(Long stickerId) {
            this.stickerId = stickerId;
            return this;
        }

        public Builder setIconId(Long iconId) {
            this.iconId = iconId;
            return this;
        }

        public Builder setGameStatus(Integer gameStatus) {
            this.gameStatus = gameStatus;
            return this;
        }

        public RoomDto build() {
            return new RoomDto(id, name, password, maxPlayers, players, currentPlayersNumber, isPrivate, userId, stickerId, iconId, gameStatus);
        }
    }
}
