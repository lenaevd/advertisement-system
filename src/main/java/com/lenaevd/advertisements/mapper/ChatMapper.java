package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.ChatDto;
import com.lenaevd.advertisements.model.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = MessageMapper.class)
public interface ChatMapper {

    @Mapping(source = "advertisement.id", target = "advertisementId")
    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "lastMessage", target = "lastMessage")
    ChatDto chatToChatDto(Chat chat);

    List<ChatDto> chatsToChatDtos(List<Chat> chats);
}
