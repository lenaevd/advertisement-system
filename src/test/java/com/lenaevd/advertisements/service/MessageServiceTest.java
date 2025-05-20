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
import com.lenaevd.advertisements.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageDao messageDao;
    @Mock
    private UserService userService;
    @Mock
    private ChatDao chatDao;
    @Mock
    private AdvertisementService adService;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Principal principal;
    private User sender;
    private User receiver;
    private Advertisement ad;
    private Chat chat;
    private Message message;
    private int adId = 10;
    private int chatId = 20;
    private int messageId = 30;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1);
        sender.setUsername("sender");

        receiver = new User();
        receiver.setId(2);
        receiver.setUsername("receiver");

        ad = new Advertisement();
        ad.setId(adId);
        ad.setSeller(receiver);

        chat = new Chat(ad, sender);
        chat.setId(chatId);
        chat.setSeller(receiver);

        message = new Message(chat, sender, "Hello");
        message.setId(messageId);
        message.setSentAt(LocalDateTime.now());

        principal = mock(Principal.class);
    }

    @Test
    void startNewChatByFirstMessage() {
        //GIVEN
        when(adService.getAdvertisementById(adId)).thenReturn(ad);
        when(chatDao.findChatByAdvertisementIdAndCustomerId(adId, 1)).thenReturn(Optional.empty());
        when(userService.getUserFromPrincipal(principal)).thenReturn(sender);

        //WHEN
        messageService.startNewChatByFirstMessage(adId, "hey", principal);

        //THEN
        verify(chatDao).save(any(Chat.class));
        verify(messageDao).save(any(Message.class));
    }

    @Test
    void startNewChatByFirstMessageAndSenderIsReceiverThrowsException() {
        //GIVEN
        when(adService.getAdvertisementById(adId)).thenReturn(ad);
        when(userService.getUserFromPrincipal(principal)).thenReturn(receiver);

        //WHEN | THEN
        ActionIsImpossibleException exception = assertThrows(ActionIsImpossibleException.class, () ->
                messageService.startNewChatByFirstMessage(adId, "Hello", principal));
        assertEquals("User can't be both sender and receiver", exception.getMessage());
    }

    @Test
    void createMessageInChat() {
        //GIVEN
        when(chatDao.findById(chatId)).thenReturn(Optional.of(chat));
        when(userService.getUserFromPrincipal(principal)).thenReturn(sender);

        //WHEN
        messageService.createMessageInChat(chatId, "message", principal);

        //THEN
        verify(messageDao).save(any(Message.class));
    }

    @Test
    void createMessageAndChatNotFoundThrowsException() {
        //GIVEN
        when(chatDao.findById(chatId)).thenReturn(Optional.empty());

        //WHEN | THEN
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                messageService.createMessageInChat(chatId, "message", principal));
        assertEquals((new ObjectNotFoundException(chatId, EntityName.CHAT).getMessage()), exception.getMessage());
        verify(messageDao, never()).save(any(Message.class));
    }

    @Test
    void getMessagesByChatId() {
        //GIVEN
        Message oldMessage = new Message(chat, sender, "lalala");
        oldMessage.setSentAt(LocalDateTime.now().minusMinutes(10));

        chat.setMessages(new ArrayList<>(List.of(message, oldMessage)));
        chat.setSeller(receiver);
        when(chatDao.findById(chatId)).thenReturn(Optional.of(chat));
        when(userService.getUserFromPrincipal(principal)).thenReturn(sender);

        //WHEN
        List<Message> result = messageService.getMessagesByChatId(chatId, principal);

        //THEN
        assertEquals(2, result.size());
        assertEquals(oldMessage, result.get(0));
        assertEquals(message, result.get(1));
    }

    @Test
    void getMessagesByChatIdThrowsNoRightsException() {
        //GIVEN
        when(chatDao.findById(chatId)).thenReturn(Optional.of(chat));
        when(userService.getUserFromPrincipal(principal)).thenReturn(new User());

        //WHEN | THEN
        NoRightsException exception = assertThrows(NoRightsException.class, () ->
                messageService.getMessagesByChatId(chatId, principal));
        assertEquals("User has no access to this chat", exception.getMessage());
    }

    @Test
    void makeMessageRead() {
        //GIVEN
        when(userService.getUserFromPrincipal(principal)).thenReturn(receiver);
        when(messageDao.findById(messageId)).thenReturn(Optional.of(message));

        //WHEN
        messageService.makeMessageRead(messageId, principal);

        //THEN
        assertTrue(message.isRead());
        verify(messageDao).update(message);
    }

    @Test
    void makeMessageReadThrowsException() {
        //GIVEN
        when(messageDao.findById(messageId)).thenReturn(Optional.of(message));
        when(userService.getUserFromPrincipal(principal)).thenReturn(new User());

        //WHEN | THEN
        assertThrows(NoRightsException.class, () -> messageService.makeMessageRead(messageId, principal));
        verify(messageDao, never()).update(any(Message.class));
    }

    @Test
    void deleteMessage() {
        //GIVEN
        when(messageDao.findById(messageId)).thenReturn(Optional.of(message));
        when(userService.getUserFromPrincipal(principal)).thenReturn(sender);

        //WHEN
        messageService.deleteMessage(messageId, principal);

        //THEN
        verify(messageDao).delete(message);
    }

    @Test
    void deleteMessageThrowsException() {
        //GIVEN
        when(messageDao.findById(messageId)).thenReturn(Optional.of(message));
        message.setSender(receiver);

        //WHEN | THEN
        assertThrows(NoRightsException.class, () -> messageService.deleteMessage(messageId, principal));
    }
}

