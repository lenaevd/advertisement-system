package com.lenaevd.advertisements.dto;

import java.time.LocalDate;

public record SaleDto(
        int id,
        int advertisementId,
        String title,
        int sellerId,
        int customerId,
        LocalDate soldAt
) {
}
