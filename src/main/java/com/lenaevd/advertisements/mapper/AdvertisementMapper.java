package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.ad.AdvertisementDto;
import com.lenaevd.advertisements.model.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AdvertisementMapper {

    @Mapping(source = "seller.id", target = "sellerId")
    AdvertisementDto adToAdDto(Advertisement adDto);

    List<AdvertisementDto> adsToAdDtos(List<Advertisement> adList);
}
