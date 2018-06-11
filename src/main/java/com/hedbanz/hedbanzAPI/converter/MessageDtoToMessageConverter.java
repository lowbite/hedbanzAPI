package com.hedbanz.hedbanzAPI.converter;

import com.hedbanz.hedbanzAPI.entity.Message;
import com.hedbanz.hedbanzAPI.transfer.MessageDto;
import com.hedbanz.hedbanzAPI.entity.Question;
import com.hedbanz.hedbanzAPI.utils.MessageTypeUtil;
import org.springframework.core.convert.converter.Converter;

public class MessageDtoToMessageConverter implements Converter<MessageDto, Message> {
    @Override
    public Message convert(MessageDto messageDto) {
        Message message = new Message();
        UserDtoToUserConverter converter = new UserDtoToUserConverter();
        message.setSenderUser(converter.convert(messageDto.getSenderUser()));
        message.setText(messageDto.getText());
        message.setType(MessageTypeUtil.convertCodeIntoEnum(messageDto.getType()));
        return message;
    }
}
