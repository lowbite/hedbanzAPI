package com.hedbanz.hedbanzAPI.utils;

import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.Message;

public class MessageUtil {
    public static MessageType convertCodeIntoEnum(Integer code){
        if(code == null){
            return null;
        }
        for(MessageType type : MessageType.values()){
            if(type.getCode() == code)
                return type;
        }
        return null;
    }
}
