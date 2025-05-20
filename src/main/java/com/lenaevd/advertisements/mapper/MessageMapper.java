package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.MessageDto;
import com.lenaevd.advertisements.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "chat.id", target = "chatId")
    @Mapping(source = "sender.id", target = "senderId")
    MessageDto messageToMessageDto(Message message);

    List<MessageDto> messagesToMessageDtos(List<Message> messages);
}
