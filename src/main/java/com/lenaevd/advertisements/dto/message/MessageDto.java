package com.lenaevd.advertisements.dto.message;

import java.time.LocalDateTime;

public record MessageDto(
        int id,
        int chatId,
        int senderId,
        String content,
        LocalDateTime sentAt,
        boolean isRead
) {
}
