package com.cgs.jt.rwis.metaservice.core.mappers;

import com.cgs.jt.rwis.metaservice.api.MeasuredParameterDTO;
import com.cgs.jt.rwis.metaservice.db.entity.MeasuredParameter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface MeasuredParameterMapper {
    MeasuredParameterDTO toDto(MeasuredParameter measuredParameter);
    List<MeasuredParameterDTO> toDtoList(List<MeasuredParameter> measuredParameterList);
    MeasuredParameter fromDto(MeasuredParameterDTO measuredParameterDTO);
}
