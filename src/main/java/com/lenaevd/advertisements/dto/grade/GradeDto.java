package com.lenaevd.advertisements.dto.grade;

import java.time.LocalDateTime;

public record GradeDto(
        int id,
        int number,
        int sellerId,
        int customerId,
        LocalDateTime createdAt
) {
}
