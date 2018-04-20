package com.hedbanz.hedbanzAPI.utils;

import com.hedbanz.hedbanzAPI.entity.Message;
import java.util.List;

public class CollectionUtil {
    public static void reverseList(List<Message> list){
        if(list != null)
            for (int i = 0; i < list.size()/2; i++) {
                Message o = list.get(i);
                list.set(i, list.get(list.size() - i - 1));
                list.set(list.size() - i - 1, o);
            }
    }
}
