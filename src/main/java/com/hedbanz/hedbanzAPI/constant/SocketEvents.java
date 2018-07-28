package com.hedbanz.hedbanzAPI.constant;

public class SocketEvents {
    //Login check events
    public static final String CLIENT_CHECK_LOGIN = "checkLogin";
    public static final String SERVER_CHECK_LOGIN = "loginResult";

    //Room events
    public static final String JOIN_ROOM_EVENT = "join-room";
    public static final String LEAVE_ROOM_EVENT = "leave-room";
    public static final String ROOM_INFO_EVENT = "joined-room";
    public static final String JOINED_USER_EVENT = "joined-user";
    public static final String LEFT_USER_EVENT = "left-user";
    public static final String CLIENT_CONNECT_INFO_EVENT = "client-connect-info";
    public static final String CLIENT_TYPING_EVENT = "client-start-typing";
    public static final String CLIENT_STOP_TYPING_EVENT = "client-stop-typing";
    public static final String CLIENT_MESSAGE_EVENT = "client-msg";
    public static final String CLIENT_SET_PLAYER_WORD_EVENT = "client-set-word";
    public static final String CLIENT_RESTORE_ROOM_EVENT = "client-restore-room";
    public static final String CLIENT_USER_GUESSING_EVENT = "client-user-guessing";
    public static final String CLIENT_USER_ANSWERING_EVENT = "client-user-answering";
    public static final String CLIENT_RESTART_GAME = "client-restart-game";
    public static final String SERVER_TYPING_EVENT = "server-start-typing";
    public static final String SERVER_STOP_TYPING_EVENT = "server-stop-typing";
    public static final String SERVER_MESSAGE_EVENT = "server-msg";
    public static final String SERVER_SET_PLAYER_WORD_EVENT = "server-set-word";
    public static final String SERVER_THOUGHT_PLAYER_WORD_EVENT = "server-thought-player-word";
    public static final String SERVER_RESTORE_ROOM_EVENT = "server-restore-room";
    public static final String SERVER_USER_AFK_EVENT = "server-user-afk";
    public static final String SERVER_USER_RETURNED_EVENT = "server-user-returned";
    public static final String SERVER_USER_GUESSING_EVENT = "server-user-guessing";
    public static final String SERVER_USER_ASKING_EVENT = "server-user-asking";
    public static final String SERVER_USER_ANSWERING_EVENT = "server-user-answering";
    public static final String SERVER_USER_WIN_EVENT = "server-user-win";
    public static final String SERVER_GAME_OVER = "server-game-over";
    public static final String SERVER_PLAYERS_STATUS = "server-players-status";

    //Afk events
    public static final String SERVER_PLAYER_AFK_WARNING = "server-player-afk-warning";
    public static final String SERVER_KICKED_USER_EVENT = "server-kicked-user";
}
