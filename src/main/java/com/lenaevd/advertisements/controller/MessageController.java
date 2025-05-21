package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.message.MessageDto;
import com.lenaevd.advertisements.dto.message.SendFirstMessageRequest;
import com.lenaevd.advertisements.dto.message.SendMessageRequest;
import com.lenaevd.advertisements.mapper.MessageMapper;
import com.lenaevd.advertisements.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Validated
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "endpoints working with messages")
public class MessageController {
    private final MessageService messageService;
    private final MessageMapper mapper;

    @PostMapping("/start")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Start chat by first message",
            description = "Supposed to use when there is no chat between users yet")
    public ResponseEntity<Void> sendFirstMessage(@RequestBody @Validated SendFirstMessageRequest request, Principal senderPrincipal) {
        messageService.startNewChatByFirstMessage(request.advertisementId(), request.content(), senderPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Send message", description = "Supposed to use when a chat already exists")
    public ResponseEntity<Void> sendMessage(@RequestBody @Validated SendMessageRequest request, Principal senderPrincipal) {
        messageService.createMessageInChat(request.chatId(), request.content(), senderPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get messages in chat")
    public ResponseEntity<List<MessageDto>> getMessagesByChatId(@RequestParam @NotNull int chatId, Principal principal) {
        return ResponseEntity.ok(mapper.messagesToMessageDtos(messageService.getMessagesByChatId(chatId, principal)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get message", description = "Allowed only for admin")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable int id) {
        return ResponseEntity.ok(mapper.messageToMessageDto(messageService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Delete message")
    public ResponseEntity<Void> deleteMessage(@PathVariable int id, Principal principal) {
        messageService.deleteMessage(id, principal);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Mark message as read")
    public ResponseEntity<Void> readMessage(@PathVariable int id, Principal principal) {
        messageService.makeMessageRead(id, principal);
        return ResponseEntity.ok().build();
    }
}
