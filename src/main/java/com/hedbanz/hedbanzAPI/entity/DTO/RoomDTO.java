package com.hedbanz.hedbanzAPI.entity.DTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedbanz.hedbanzAPI.deserializer.RoomDeserializer;
import com.hedbanz.hedbanzAPI.entity.error.CustomError;

import java.util.List;


@JsonDeserialize(using = RoomDeserializer.class)
public class RoomDTO {
    private Long id;
    private String name;
    private String password;
    private Integer maxPlayers;
    private List<PlayerDTO> players;
    private Integer currentPlayersNumber;
    private Boolean isPrivate;

    public RoomDTO(){

    }

    public RoomDTO(Long id, String name, Integer maxPlayers, Integer currentPlayersNumber, Boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayersNumber = currentPlayersNumber;
        this.isPrivate = isPrivate;
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

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
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
}
