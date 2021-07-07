package com.cgs.jt.rwis.metaservice.core.mappers;

import com.cgs.jt.rwis.metaservice.api.ParameterOnStationDTO;
import com.cgs.jt.rwis.metaservice.db.entity.ParameterOnStation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ParameterOnStationMapper {
    ParameterOnStationDTO toDto(ParameterOnStation parameterOnStation);
    List<ParameterOnStationDTO> toDtoList(List<ParameterOnStation> parameterOnStationList);
    ParameterOnStation fromDto(ParameterOnStationDTO parameterOnStationDTO);
}
