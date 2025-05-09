package com.lenaevd.advertisements.dto.grade;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LeaveGradeRequest(
        @Min(value = 1)
        @Max(value = 5)
        int number,
        @NotNull
        int userToRateId
) {
}
