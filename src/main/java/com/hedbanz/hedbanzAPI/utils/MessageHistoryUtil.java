package com.hedbanz.hedbanzAPI.utils;

import com.hedbanz.hedbanzAPI.constant.MessageType;
import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.transfer.QuestionDto;
import com.hedbanz.hedbanzAPI.transfer.WordSettingDto;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.stream.Collectors;

public class MessageHistoryUtil {
    /*public static List<MessageDto> convertToDto(List<Message> messages, List<Player> players, ConversionService conversionService){
        return messages.stream().map(message -> {
            if (message.getType() == MessageType.USER_QUESTION) {
                return conversionService.convert(message, QuestionDto.class);
            }else if(message.getType() == MessageType.WORD_SETTING){
                WordSettingDto wordSettingDto = conversionService.convert(message, WordSettingDto.class);
                Player player = getPlayerByUserId(players, message.getSenderUser().getUserId());
                if(player != null){
                    wordSettingDto.setWordReceiverUser(player.getWordSettingUserId());
                    player = getPlayerByUserId( players, wordSettingDto.getWordReceiverUser());
                    if(player != null)
                        wordSettingDto.setWord(player.getWord());
                }
                return wordSettingDto;
            }else {
                return conversionService.convert(message, MessageDto.class);
            }
        }).collect(Collectors.toList());
    }

    private static Player getPlayerByUserId(List<Player> players, Long userId){
        for (Player player: players) {
            if(player.getUser().getUserId().equals(userId)){
                return player;
            }
        }
        return null;
    }*/
}

