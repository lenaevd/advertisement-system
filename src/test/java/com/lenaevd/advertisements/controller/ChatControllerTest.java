package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.ChatDto;
import com.lenaevd.advertisements.dto.MessageDto;
import com.lenaevd.advertisements.mapper.ChatMapper;
import com.lenaevd.advertisements.model.Chat;
import com.lenaevd.advertisements.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {
    @Mock
    private ChatService chatService;
    @Mock
    private ChatMapper mapper;

    @InjectMocks
    private ChatController chatController;

    private Principal principal;
    private Chat chat;
    private ChatDto chatDto;
    private List<Chat> chats;
    private List<ChatDto> chatDtos;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        chat = new Chat();
        chatDto = new ChatDto(1, 1, 1, 10,
                new MessageDto(4, 1, 10, "message", LocalDateTime.of(2022, 2, 2, 10, 0), true));
        chats = List.of(chat);
        chatDtos = List.of(chatDto);
    }

    @Test
    void getUsersChats() {
        //GIVEN
        when(chatService.getUsersChats(principal)).thenReturn(chats);
        when(mapper.chatsToChatDtos(chats)).thenReturn(chatDtos);

        //WHEN
        ResponseEntity<List<ChatDto>> response = chatController.getUsersChats(principal);

        //THEN
        verify(chatService).getUsersChats(principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chatDtos, response.getBody());
    }

    @Test
    void getChatById() {
        //GIVEN
        int chatId = 1;
        when(chatService.getById(chatId)).thenReturn(chat);
        when(mapper.chatToChatDto(chat)).thenReturn(chatDto);

        //WHEN
        ResponseEntity<ChatDto> response = chatController.getChatById(chatId);

        //THEN
        verify(chatService).getById(chatId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chatDto, response.getBody());
    }

    @Test
    void deleteChatById() {
        //GIVEN
        int chatId = 1;

        //WHEN
        ResponseEntity<Void> response = chatController.deleteChatById(chatId);

        //THEN
        verify(chatService).deleteById(chatId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }
}
