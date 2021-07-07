package com.cgs.jt.rwis.metaservice.core;

import com.cgs.jt.rwis.metaservice.api.StationDTO;
import com.cgs.jt.rwis.metaservice.core.mappers.StationMapper;
import com.cgs.jt.rwis.metaservice.db.dao.StationDAO;
import com.cgs.jt.rwis.metaservice.db.entity.Station;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class StationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StationService.class);
    private static StationMapper mapper = Mappers.getMapper(StationMapper.class);

    @Inject
    private StationDAO dao;

    public StationDTO createStation(StationDTO stationDTO) {
        LOGGER.info("Creating station: " + stationDTO.toString());

        Station station = mapper.fromDto(stationDTO);
        Station createdStation = dao.create(station);
        StationDTO createdStationDTO = mapper.toDto(createdStation);

        return createdStationDTO;
    }

    public List<StationDTO> getStations() {
        LOGGER.info("Getting all stations.");

        List<Station> stationList = dao.findAll();
        List<StationDTO> stationDTOList = mapper.toDtoList(stationList);

        return stationDTOList;
    }

    public StationDTO getStation(int id) {
        LOGGER.info("Getting station with ID: " + id);

        Optional<Station> s = dao.find(id);
        if (s.isPresent()) {
            Station station = s.get();
            StationDTO stationDTO = mapper.toDto(station);

            return stationDTO;
        }
        throw new WebApplicationException("Stationd with ID " + id + " not found", Response.Status.NOT_FOUND);
    }

    public StationDTO updateStation(int id, StationDTO stationDTO) {
        LOGGER.info("Updating stationd with ID: " + id + ". New data: " + stationDTO.toString());

        Station station = mapper.fromDto(stationDTO);

        Optional<Station> s = dao.update(id, station);
        if (s.isPresent()) {
            Station updatedStation = s.get();
            StationDTO updatedStationDTO = mapper.toDto(updatedStation);

            return updatedStationDTO;
        }
        throw new WebApplicationException("Station with ID " + id + " not found", Response.Status.NOT_FOUND);
    }

    public StationDTO deleteStation(int id) {
        LOGGER.info("Deleting station with ID: " + id);

        Optional<Station> s = dao.delete(id);
        if (s.isPresent()) {
            Station deletedStation = s.get();
            StationDTO deletedStationDTO = mapper.toDto(deletedStation);

            return deletedStationDTO;
        }
        throw new WebApplicationException("Station with ID " + id + " not found", Response.Status.NOT_FOUND);
    }
}

