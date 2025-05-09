package com.lenaevd.advertisements.mapper;

import com.lenaevd.advertisements.dto.grade.GradeDto;
import com.lenaevd.advertisements.model.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface GradeMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "seller.id", target = "sellerId")
    GradeDto gradeToGradeDto(Grade grade);

    List<GradeDto> gradesToGradeDtos(List<Grade> grades);
}
