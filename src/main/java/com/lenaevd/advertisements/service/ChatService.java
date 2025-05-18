package com.lenaevd.advertisements.service;

import com.lenaevd.advertisements.model.Chat;

import java.security.Principal;
import java.util.List;

public interface ChatService {
    List<Chat> getUsersChats(Principal principal);

    Chat getById(int id);

    void deleteById(int id);
}
