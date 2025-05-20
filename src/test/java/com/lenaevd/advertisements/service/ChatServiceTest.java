package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.dao.ChatDao;
import com.lenaevd.advertisements.exception.ObjectNotFoundException;
import com.lenaevd.advertisements.model.Advertisement;
import com.lenaevd.advertisements.model.Chat;
import com.lenaevd.advertisements.model.EntityName;
import com.lenaevd.advertisements.model.Message;
import com.lenaevd.advertisements.model.User;
import com.lenaevd.advertisements.service.impl.ChatServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @Mock
    private ChatDao chatDao;
    @Mock
    private UserService userService;

    @InjectMocks
    private ChatServiceImpl chatService;

    private int id = 1;

    @Test
    void getUsersChats() {
        //GIVEN
        User user = new User();
        int userId = 10;
        user.setId(userId);
        user.setUsername("user");

        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        when(userService.getUserFromPrincipal(principal)).thenReturn(user);

        Message message1 = new Message();
        message1.setSentAt(LocalDateTime.now().minusMinutes(10));

        Message message2 = new Message();
        message2.setSentAt(LocalDateTime.now().minusMinutes(1));

        Message message3 = new Message();
        message3.setSentAt(LocalDateTime.now().minusMinutes(5));

        Chat chat1 = new Chat(new Advertisement(), user);
        chat1.setId(1);
        chat1.setLastMessage(message1);

        Chat chat2 = new Chat(new Advertisement(), user);
        chat2.setId(2);
        chat2.setLastMessage(message2);

        Chat chat3 = new Chat(new Advertisement(), user);
        chat3.setId(3);
        chat3.setLastMessage(message3);

        List<Chat> unsortedChats = new ArrayList<>(List.of(chat1, chat2, chat3));

        when(chatDao.findChatsByUserId(userId)).thenReturn(unsortedChats);

        //WHEN
        List<Chat> result = chatService.getUsersChats(principal);

        //THEN
        assertThat(result).containsExactly(chat2, chat3, chat1);
    }

    @Test
    void getById() {
        //GIVEN
        Chat chat = new Chat();
        chat.setId(id);
        when(chatDao.findById(id)).thenReturn(Optional.of(chat));

        //WHEN
        Chat resultChat = chatService.getById(id);

        //THEN
        assertEquals(chat, resultChat);
    }

    @Test
    void getByIdAndChatNotFoundThrowException() {
        //GIVEN
        when(chatDao.findById(id)).thenReturn(Optional.empty());

        //WHEN | THEN
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> chatService.getById(id));
        assertEquals(new ObjectNotFoundException(id, EntityName.CHAT).getMessage(), exception.getMessage());
    }

    @Test
    void deleteById() {
        //GIVEN
        Chat chat = new Chat();
        when(chatDao.findById(id)).thenReturn(Optional.of(chat));

        //WHEN
        chatService.deleteById(id);

        //THEN
        verify(chatDao).delete(chat);
    }
}
