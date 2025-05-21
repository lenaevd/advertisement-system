package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.ChatDto;
import com.lenaevd.advertisements.mapper.ChatMapper;
import com.lenaevd.advertisements.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "Chats", description = "endpoints to get users chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get personal chats", description = "Returns chats of authenticated user")
    public ResponseEntity<List<ChatDto>> getUsersChats(Principal principal) {
        return ResponseEntity.ok(mapper.chatsToChatDtos(chatService.getUsersChats(principal)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get chat by id", description = "Allowed only for admin")
    public ResponseEntity<ChatDto> getChatById(@PathVariable int id) {
        return ResponseEntity.ok(mapper.chatToChatDto(chatService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete chat by id", description = "Allowed only for admin")
    public ResponseEntity<Void> deleteChatById(@PathVariable int id) {
        chatService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
