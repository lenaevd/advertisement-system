package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dao.ChatDao;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.Chat;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.Message;
import com.lenaevd.advertisements.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatDao chatDao;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Chat> getUsersChats(Principal principal) {
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
        return chats;
    }

    @Transactional(readOnly = true)
    public Chat getById(int id) {
        return chatDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.CHAT));
    }

    @Transactional
    public void deleteById(int id) {
        chatDao.deleteById(id);
    }
}
