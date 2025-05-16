package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.MessageDto;
import com.lenaevd.advertisements.dto.SendFirstMessageRequest;
import com.lenaevd.advertisements.dto.SendMessageRequest;
import com.lenaevd.advertisements.mapper.MessageMapper;
import com.lenaevd.advertisements.service.MessageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final MessageMapper mapper;

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> sendFirstMessage(@RequestBody @Validated SendFirstMessageRequest request, Principal senderPrincipal) {
        messageService.startNewChatByFirstMessage(request.advertisementId(), request.content(), senderPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> sendMessage(@RequestBody @Validated SendMessageRequest request, Principal senderPrincipal) {
        messageService.createMessageInChat(request.chatId(), request.content(), senderPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<MessageDto>> getMessagesByChatId(@RequestParam @NotNull int chatId, Principal principal) {
        return ResponseEntity.ok(mapper.messagesToMessageDtos(messageService.getMessagesByChatId(chatId, principal)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable int id) {
        return ResponseEntity.ok(mapper.messageToMessageDto(messageService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> deleteMessage(@PathVariable int id, Principal principal) {
        messageService.deleteMessage(id, principal);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Void> readMessage(@PathVariable int id, Principal principal) {
        messageService.makeMessageRead(id, principal);
        return ResponseEntity.ok().build();
    }
}
