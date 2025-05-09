package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.SaleDto;
import com.lenaevd.advertisements.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SaleMapper {

    @Mapping(source = "advertisement.id", target = "advertisementId")
    @Mapping(source = "advertisement.seller.id", target = "sellerId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "advertisement.title", target = "title")
    SaleDto saleToSaleDto(Sale sale);

    List<SaleDto> salesToSaleDtos(List<Sale> sales);
}
