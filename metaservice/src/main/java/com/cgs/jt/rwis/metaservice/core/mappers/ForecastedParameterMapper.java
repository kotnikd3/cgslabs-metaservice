package com.cgs.jt.rwis.metaservice.core.mappers;

import com.cgs.jt.rwis.metaservice.api.ForecastedParameterDTO;
import com.cgs.jt.rwis.metaservice.db.entity.ForecastedParameter;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ForecastedParameterMapper {
    ForecastedParameterDTO toDto(ForecastedParameter forecastedParameter);
    List<ForecastedParameterDTO> toDtoList(List<ForecastedParameter> forecastedParameterList);
    ForecastedParameter fromDto(ForecastedParameterDTO forecastedParameterDTO);
}
