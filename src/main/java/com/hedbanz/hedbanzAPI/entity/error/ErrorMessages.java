package com.hedbanz.hedbanzAPI.entity.error;

public interface ErrorMessages {
     String NO_SUCH_USER_MESSAGE = "Such user did not found!";
     String INCORRECT_PASSWORD_MESSAGE = "Incorrect password!";
     String EMPTY_LOGIN_MESSAGE = "Empty login field!";
     String EMPTY_PASSWORD_MESSAGE = "Empty password field!";
     String EMPTY_EMAIL_MESSAGE = "Empty email field!";
     String SUCH_LOGIN_ALREADY_EXIST_MESSAGE = "Such login already exist!";
     String SUCH_EMAIL_ALREADY_USING_MESSAGE = "Such email already using!";
     String INVALID_PASSWORD = "Invalid password!";
     String INVALID_LOGIN = "Invalid login!";
     String INVALID_EMAIL = "Invalid email!";
     String ALREADY_FRIENDS = "You are already friends!";
     String CANT_SEND_FRIENDSHIP_REQUEST = "Can't send friendship request to this user at this time";
     String USER_ALREADY_IN_ROOM = "This user is already in that room!";
     String NO_SUCH_USER_IN_ROOM = "There is no such user in room!";


     String INCORRECT_INPUT = "Incorrect input!";

     String ROOM_IS_FULL = "Room is full!";

     String DB_ERROR = "Problems with database actions!";

}