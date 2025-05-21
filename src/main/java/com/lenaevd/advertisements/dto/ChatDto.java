package com.lenaevd.advertisements.dto;

import com.lenaevd.advertisements.dto.message.MessageDto;

public record ChatDto(
        int id,
        int advertisementId,
        int sellerId,
        int customerId,
        MessageDto lastMessage
) {
}
