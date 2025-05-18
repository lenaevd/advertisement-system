package com.lenaevd.advertisements.dao;

import com.lenaevd.advertisements.model.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatDao extends Dao<Chat> {
    List<Chat> findChatsByUserId(int userId);

    Optional<Chat> findChatByAdvertisementIdAndCustomerId(int adId, int customerId);
}
