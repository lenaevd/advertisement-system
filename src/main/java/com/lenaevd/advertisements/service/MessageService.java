package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.model.Message;

import java.security.Principal;
import java.util.List;

public interface MessageService {
    void startNewChatByFirstMessage(int adId, String content, Principal senderPrincipal);

    void createMessageInChat(int chatId, String content, Principal senderPrincipal);

    List<Message> getMessagesByChatId(int chatId, Principal principal);

    void makeMessageRead(int id, Principal principal);

    Message getById(int id);

    void deleteMessage(int id, Principal principal);
}
