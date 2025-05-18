package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.SaleDto;
import com.lenaevd.advertisements.mapper.SaleMapper;
import com.lenaevd.advertisements.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class SalesHistoryController {
    private final SaleService saleService;
    private final SaleMapper saleMapper;

    @GetMapping("/sold")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<SaleDto>> getSoldItems(Principal principal) {
        return ResponseEntity.ok(saleMapper.salesToSaleDtos(saleService.getUsersSoldItems(principal)));
    }

    @GetMapping("/purchased")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<SaleDto>> getPurchasedItems(Principal principal) {
        return ResponseEntity.ok(saleMapper.salesToSaleDtos(saleService.getUsersPurchasedItems(principal)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<SaleDto>> getAllSales() {
        return ResponseEntity.ok(saleMapper.salesToSaleDtos(saleService.getAllSales()));
    }
}
