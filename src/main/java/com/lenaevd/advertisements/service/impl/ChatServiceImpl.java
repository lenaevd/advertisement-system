package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.config.LoggerMessages;
import com.lenaevd.advertisements.dao.ChatDao;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.Chat;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.Message;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.ChatService;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatDao chatDao;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<Chat> getUsersChats(Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_USER, "getUsersChats", EntityName.USER, principal.getName());
        User user = userService.getUserFromPrincipal(principal);
        List<Chat> chats = chatDao.findChatsByUserId(user.getId());

        chats.sort((chat1, chat2) -> {
            Message m1 = chat1.getLastMessage();
            Message m2 = chat2.getLastMessage();
            if (m1.isRead() != m2.isRead()) {
                return Boolean.compare(m2.isRead(), m1.isRead());
            }
            return m2.getSentAt().compareTo(m1.getSentAt());
        });
        LOGGER.info("Returned {} chats for user {}", chats.size(), user.getUsername());
        return chats;
    }

    @Override
    @Transactional(readOnly = true)
    public Chat getById(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getById", EntityName.CHAT, id);
        return chatDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.CHAT));
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "deleteById", EntityName.CHAT, id);
        Chat chat = getById(id);
        chatDao.delete(chat);
        LOGGER.info(LoggerMessages.DELETE_COMPLETED, EntityName.CHAT, id);
    }
}
