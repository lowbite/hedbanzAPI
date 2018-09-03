package com.hedbanz.hedbanzAPI.utils;

public class MessageHistoryUtil {
    /*public static List<MessageDto> convertToDto(List<Message> messages, List<Player> players, ConversionService conversionService){
        return messages.stream().map(message -> {
            if (message.getType() == MessageType.USER_QUESTION) {
                return conversionService.convert(message, QuestionDto.class);
            }else if(message.getType() == MessageType.WORD_SETTING){
                SetWordDto wordSettingDto = conversionService.convert(message, SetWordDto.class);
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

