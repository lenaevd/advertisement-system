package com.lenaevd.advertisements.dto;

public record ChatDto(
        int id,
        int advertisementId,
        int sellerId,
        int customerId,
        MessageDto lastMessage
) {
}
