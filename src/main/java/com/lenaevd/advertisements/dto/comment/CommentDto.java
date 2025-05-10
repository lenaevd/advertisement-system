package com.lenaevd.advertisements.dto.comment;

import java.time.LocalDateTime;

public record CommentDto(
        int id,
        int advertisementId,
        int authorId,
        String content,
        LocalDateTime createdAt
) {
}
