package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.MessageDto;
import com.lenaevd.advertisements.model.Message;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    default MessageDto messageToMessageDto(Message message) {
        if (message == null) {
            return null;
        }
        return new MessageDto(message.getId(), message.getChat().getId(), message.getSender().getId(),
                message.getContent(), message.getSentAt(), message.isRead());
    }

    List<MessageDto> messagesToMessageDtos(List<Message> messages);
}
