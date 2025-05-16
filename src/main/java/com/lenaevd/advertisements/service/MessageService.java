package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dao.ChatDao;
import com.lenaevd.advertisements.dao.MessageDao;
import com.lenaevd.advertisements.exception.ActionIsImpossibleException;
import com.lenaevd.advertisements.exception.NoRightsException;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.Chat;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.Message;
import com.lenaevd.advertisements.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageDao messageDao;
    private final UserService userService;
    private final ChatDao chatDao;
    private final AdvertisementService adService;

    @Transactional
    public void startNewChatByFirstMessage(int adId, String content, Principal senderPrincipal) {
        Advertisement ad = adService.getAdvertisementByIdOrElseThrow(adId);
        User sender = userService.getUserFromPrincipal(senderPrincipal);

        if (ad.getSeller().equals(sender)) {
            throw new ActionIsImpossibleException("User can't be both sender and receiver");
        }

        Optional<Chat> chatOptional = chatDao.findChatByAdvertisementIdAndCustomerId(adId, sender.getId());
        Chat chat;
        if (chatOptional.isPresent()) {
            chat = chatOptional.get();
        } else {
            chat = new Chat(ad, sender);
            chatDao.save(chat);
        }

        Message message = new Message(chat, sender, content);
        messageDao.save(message);
    }

    @Transactional
    public void createMessageInChat(int chatId, String content, Principal senderPrincipal) {
        Optional<Chat> chatOptional = chatDao.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ObjectNotFoundException(chatId, EntityName.CHAT);
        } else {
            User sender = userService.getUserFromPrincipal(senderPrincipal);
            Message message = new Message(chatOptional.get(), sender, content);
            messageDao.save(message);
        }
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByChatId(int chatId, Principal principal) {
        Optional<Chat> chatOptional = chatDao.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ObjectNotFoundException(chatId, EntityName.CHAT);
        } else {
            Chat chat = chatOptional.get();
            User user = userService.getUserFromPrincipal(principal);
            if (chat.getSeller().equals(user) || chat.getCustomer().equals(user)) {
                List<Message> messages = chat.getMessages();
                messages.sort(Comparator.comparing(Message::getSentAt));
                return messages;
            } else {
                throw new NoRightsException("User has no access to this chat");
            }
        }
    }

    @Transactional
    public void deleteMessage(int id, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Message message = messageDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.MESSAGE));
        if (message.getSender().equals(user)) {
            messageDao.deleteById(id);
        } else {
            throw new NoRightsException("User is not the author of the message");
        }
    }

    @Transactional
    public void makeMessageRead(int id, Principal principal) {
        User user = userService.getUserFromPrincipal(principal);
        Message message = messageDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.MESSAGE));
        if (!message.getSender().equals(user) && message.getChat().getMembers().contains(user)) {
            message.setRead(true);
            messageDao.update(message);
        } else {
            throw new NoRightsException("User is not the author of the message");
        }
    }

    @Transactional(readOnly = true)
    public Message getById(int id) {
        return messageDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.MESSAGE));
    }
}
