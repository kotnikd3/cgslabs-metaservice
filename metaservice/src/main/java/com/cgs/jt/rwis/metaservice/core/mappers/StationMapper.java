package com.cgs.jt.rwis.metaservice.core.mappers;

import com.cgs.jt.rwis.metaservice.api.StationDTO;
import com.cgs.jt.rwis.metaservice.db.entity.Station;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {
    StationDTO toDto(Station station);
    List<StationDTO> toDtoList(List<Station> stationList);
    Station fromDto(StationDTO stationDTO);
}
