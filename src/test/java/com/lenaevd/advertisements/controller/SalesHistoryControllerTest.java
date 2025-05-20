package com.lenaevd.advertisements.controller;

import com.lenaevd.advertisements.dto.SaleDto;
import com.lenaevd.advertisements.mapper.SaleMapper;
import com.lenaevd.advertisements.model.Sale;
import com.lenaevd.advertisements.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class SalesHistoryControllerTest {
    @Mock
    private SaleService saleService;
    @Mock
    private SaleMapper saleMapper;
    @InjectMocks
    private SalesHistoryController salesHistoryController;

    private Principal principal;
    private List<Sale> sales;
    private List<SaleDto> saleDtos;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        Sale sale = new Sale();
        SaleDto saleDto = new SaleDto(1, 1, "title", 1, 2, LocalDate.of(2020, 1, 1));
        sales = List.of(sale);
        saleDtos = List.of(saleDto);
    }

    @Test
    void getSoldItems() {
        //GIVEN
        when(saleService.getUsersSoldItems(principal)).thenReturn(sales);
        when(saleMapper.salesToSaleDtos(sales)).thenReturn(saleDtos);

        //WHEN
        ResponseEntity<List<SaleDto>> response = salesHistoryController.getSoldItems(principal);

        //THEN
        verify(saleService).getUsersSoldItems(principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(saleDtos, response.getBody());
    }

    @Test
    void getPurchasedItems() {
        //GIVEN
        when(saleService.getUsersPurchasedItems(principal)).thenReturn(sales);
        when(saleMapper.salesToSaleDtos(sales)).thenReturn(saleDtos);

        //WHEN
        ResponseEntity<List<SaleDto>> response = salesHistoryController.getPurchasedItems(principal);

        //THEN
        verify(saleService).getUsersPurchasedItems(principal);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(saleDtos, response.getBody());
    }

    @Test
    void getAllSales() {
        //GIVEN
        when(saleService.getAllSales()).thenReturn(sales);
        when(saleMapper.salesToSaleDtos(sales)).thenReturn(saleDtos);

        //WHEN
        ResponseEntity<List<SaleDto>> response = salesHistoryController.getAllSales();

        //THEN
        verify(saleService).getAllSales();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(saleDtos, response.getBody());
    }
}
