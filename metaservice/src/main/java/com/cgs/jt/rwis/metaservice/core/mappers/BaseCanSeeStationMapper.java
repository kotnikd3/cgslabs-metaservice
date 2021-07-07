package com.cgs.jt.rwis.metaservice.core.mappers;

import com.cgs.jt.rwis.metaservice.api.BaseCanSeeStationDTO;
import com.cgs.jt.rwis.metaservice.db.entity.BaseCanSeeStation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BaseCanSeeStationMapper {
    BaseCanSeeStationDTO toDto(BaseCanSeeStation baseCanSeeStation);
    List<BaseCanSeeStationDTO> toDtoList(List<BaseCanSeeStation> baseCanSeeStationList);
    BaseCanSeeStation fromDto(BaseCanSeeStationDTO baseCanSeeStationDTO);
}
