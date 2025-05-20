package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.MessageDto;
import com.lenaevd.advertisements.dto.SendFirstMessageRequest;
import com.lenaevd.advertisements.dto.SendMessageRequest;
import com.lenaevd.advertisements.mapper.MessageMapper;
import com.lenaevd.advertisements.model.Message;
import com.lenaevd.advertisements.service.MessageService;
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
class MessageControllerTest {
    @Mock
    private MessageService messageService;
    @Mock
    private MessageMapper mapper;

    @InjectMocks
    private MessageController messageController;

    private Principal principal;
    private Message message;
    private MessageDto messageDto;
    int messageId = 1;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        message = new Message();
        messageDto = new MessageDto(1, 1, 2, "message", LocalDateTime.now(), false);
    }

    @Test
    void sendFirstMessage() {
        //GIVEN
        SendFirstMessageRequest request = new SendFirstMessageRequest(1, "first message");

        //WHEN
        ResponseEntity<Void> response = messageController.sendFirstMessage(request, principal);

        //THEN
        verify(messageService).startNewChatByFirstMessage(request.advertisementId(), request.content(), principal);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void sendMessage() {
        //GIVEN
        SendMessageRequest request = new SendMessageRequest(5, "message to chat");

        //WHEN
        ResponseEntity<Void> response = messageController.sendMessage(request, principal);

        //THEN
        verify(messageService).createMessageInChat(request.chatId(), request.content(), principal);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getMessagesByChatId() {
        //GIVEN
        int chatId = 5;
        List<Message> messages = List.of(message);
        List<MessageDto> messageDtos = List.of(messageDto);
        when(messageService.getMessagesByChatId(chatId, principal)).thenReturn(messages);
        when(mapper.messagesToMessageDtos(messages)).thenReturn(messageDtos);

        //WHEN
        ResponseEntity<List<MessageDto>> response = messageController.getMessagesByChatId(chatId, principal);

        //THEN
        verify(messageService).getMessagesByChatId(chatId, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageDtos, response.getBody());
    }

    @Test
    void getMessageById() {
        //GIVEN
        when(messageService.getById(messageId)).thenReturn(message);
        when(mapper.messageToMessageDto(message)).thenReturn(messageDto);

        //WHEN
        ResponseEntity<MessageDto> response = messageController.getMessageById(messageId);

        //THEN
        verify(messageService).getById(messageId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messageDto, response.getBody());
    }

    @Test
    void deleteMessage() {
        //WHEN
        ResponseEntity<Void> response = messageController.deleteMessage(messageId, principal);

        //THEN
        verify(messageService).deleteMessage(messageId, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void readMessage() {
        //WHEN
        ResponseEntity<Void> response = messageController.readMessage(messageId, principal);

        //THEN
        verify(messageService).makeMessageRead(messageId, principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }
}