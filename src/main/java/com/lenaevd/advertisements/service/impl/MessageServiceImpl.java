package com.lenaevd.advertisements.service.impl;

import com.lenaevd.advertisements.config.LoggerMessages;
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
import com.lenaevd.advertisements.service.AdvertisementService;
import com.lenaevd.advertisements.service.MessageService;
import com.lenaevd.advertisements.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);
    private static final String USER_IS_NOT_THE_AUTHOR = "User is not the author of the message";
    private static final String NEW_MESSAGE_LOG = "Created new message from {}";

    private final MessageDao messageDao;
    private final UserService userService;
    private final ChatDao chatDao;
    private final AdvertisementService adService;

    @Override
    @Transactional
    public void startNewChatByFirstMessage(int adId, String content, Principal senderPrincipal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "startNewChatByFirstMessage", EntityName.ADVERTISEMENT, adId);
        Advertisement ad = adService.getAdvertisementById(adId);
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
            LOGGER.info("Created new chat for users {},{}", ad.getSeller().getUsername(), sender.getUsername());
        }

        Message message = new Message(chat, sender, content);
        messageDao.save(message);
        LOGGER.info(NEW_MESSAGE_LOG, sender.getUsername());
    }

    @Override
    @Transactional
    public void createMessageInChat(int chatId, String content, Principal senderPrincipal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "createMessageInChat", EntityName.CHAT, chatId);
        Optional<Chat> chatOptional = chatDao.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ObjectNotFoundException(chatId, EntityName.CHAT);
        } else {
            User sender = userService.getUserFromPrincipal(senderPrincipal);
            Message message = new Message(chatOptional.get(), sender, content);
            messageDao.save(message);
            LOGGER.info(NEW_MESSAGE_LOG, sender.getUsername());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessagesByChatId(int chatId, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getMessagesByChatId", EntityName.CHAT, chatId);
        Optional<Chat> chatOptional = chatDao.findById(chatId);
        if (chatOptional.isEmpty()) {
            throw new ObjectNotFoundException(chatId, EntityName.CHAT);
        } else {
            Chat chat = chatOptional.get();
            User user = userService.getUserFromPrincipal(principal);
            if (chat.getSeller().equals(user) || chat.getCustomer().equals(user)) {
                List<Message> messages = chat.getMessages();
                messages.sort(Comparator.comparing(Message::getSentAt));
                LOGGER.info("Found messages for chat id={}", chatId);
                return messages;
            } else {
                throw new NoRightsException("User has no access to this chat");
            }
        }
    }

    @Override
    @Transactional
    public void makeMessageRead(int id, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "makeMessageRead", EntityName.MESSAGE, id);
        User user = userService.getUserFromPrincipal(principal);
        Message message = messageDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.MESSAGE));
        if (!message.getSender().equals(user) && message.getChat().getMembers().contains(user)) {
            message.setRead(true);
            messageDao.update(message);
            LOGGER.info("Message with id={} is read", message.getId());
        } else {
            throw new NoRightsException("This message can't be read by user");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Message getById(int id) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "getById", EntityName.MESSAGE, id);
        return messageDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.MESSAGE));
    }

    @Override
    @Transactional
    public void deleteMessage(int id, Principal principal) {
        LOGGER.debug(LoggerMessages.EXECUTING_FOR_OBJECT, "deleteMessage", EntityName.MESSAGE, id);
        User user = userService.getUserFromPrincipal(principal);
        Message message = messageDao.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, EntityName.MESSAGE));
        if (message.getSender().equals(user)) {
            messageDao.delete(message);
            LOGGER.info(LoggerMessages.DELETE_COMPLETED, EntityName.MESSAGE, id);
        } else {
            throw new NoRightsException(USER_IS_NOT_THE_AUTHOR);
        }
    }
}
