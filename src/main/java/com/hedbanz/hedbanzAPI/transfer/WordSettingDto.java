package com.hedbanz.hedbanzAPI.transfer;

import java.util.Date;

public class WordSettingDto extends MessageDto{
    private String word;
    private UserDto wordReceiverUser;

    public WordSettingDto() {
    }

    WordSettingDto(Long clientMessageId, UserDto senderUser, Long roomId, String text, Integer type, Date createDate,
                   String word, UserDto wordReceiverUser) {
        super(clientMessageId, senderUser, roomId, text, type, createDate);
        this.word = word;
        this.wordReceiverUser = wordReceiverUser;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public UserDto getWordReceiverUser() {
        return wordReceiverUser;
    }

    public void setWordReceiverUser(UserDto wordReceiverUser) {
        this.wordReceiverUser = wordReceiverUser;
    }

    public static class Builder {
        private Long clientMessageId;
        private UserDto senderUser;
        private Long roomId;
        private String text;
        private Integer type;
        private Date createDate;
        private String word;
        private UserDto wordReceiverUser;

        public Builder setClientMessageId(Long clientMessageId) {
            this.clientMessageId = clientMessageId;
            return this;
        }

        public Builder setSenderUser(UserDto senderUser) {
            this.senderUser = senderUser;
            return this;
        }

        public Builder setRoomId(Long roomId) {
            this.roomId = roomId;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setType(Integer type) {
            this.type = type;
            return this;
        }

        public Builder setCreateDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        public Builder setWord(String word) {
            this.word = word;
            return this;
        }

        public Builder setWordReceiverUser(UserDto wordReceiverUser) {
            this.wordReceiverUser = wordReceiverUser;
            return this;
        }

        public WordSettingDto build() {
            return new WordSettingDto(clientMessageId, senderUser, roomId, text, type, createDate, word, wordReceiverUser);
        }
    }
}
