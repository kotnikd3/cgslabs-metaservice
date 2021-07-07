package com.cgs.jt.rwis.metaservice.core;

import com.cgs.jt.rwis.metaservice.api.ForecastedParameterDTO;
import com.cgs.jt.rwis.metaservice.core.mappers.ForecastedParameterMapper;
import com.cgs.jt.rwis.metaservice.db.dao.ForecastedParameterDAO;
import com.cgs.jt.rwis.metaservice.db.entity.ForecastedParameter;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class ForecastedParameterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForecastedParameterService.class);
    private static ForecastedParameterMapper mapper = Mappers.getMapper(ForecastedParameterMapper.class);

    @Inject
    private ForecastedParameterDAO dao;

    public ForecastedParameterDTO createParameter(ForecastedParameterDTO parameterDTO) {
        LOGGER.info("Creating parameter: " + parameterDTO.toString());

        ForecastedParameter parameter = mapper.fromDto(parameterDTO);
        ForecastedParameter createdParameter = dao.create(parameter);
        ForecastedParameterDTO createdParameterDTO = mapper.toDto(createdParameter);

        return createdParameterDTO;
    }

    public List<ForecastedParameterDTO> getParameters() {
        LOGGER.info("Getting all parameters.");

        List<ForecastedParameter> parameterList = dao.findAll();
        List<ForecastedParameterDTO> parameterDTOList = mapper.toDtoList(parameterList);

        return parameterDTOList;
    }

    public ForecastedParameterDTO getParameter(String name) {
        LOGGER.info("Getting parameter with name: " + name);

        Optional<ForecastedParameter> p = dao.findByName(name);
        if (p.isPresent()) {
            ForecastedParameter parameter = p.get();
            ForecastedParameterDTO parameterDTO = mapper.toDto(parameter);

            return parameterDTO;
        }
        throw new WebApplicationException("Parameter with name " + name + " not found", Response.Status.NOT_FOUND);
    }

    /*public ParameterDTO updateParameter(String name, ParameterDTO parameterDTO) {
        LOGGER.info("Updating parameter with name: " + name + ". New data: " + parameterDTO.toString());

        Parameter parameter = mapper.fromDto(parameterDTO);

        Optional<Parameter> p = dao.update(name, parameter);
        if (p.isPresent()) {
            Parameter updatedParameter = p.get();
            ParameterDTO updatedParameterDTO = mapper.toDto(updatedParameter);

            return updatedParameterDTO;
        }
        throw new WebApplicationException("Parameter with ID " + name + " not found", Response.Status.NOT_FOUND);
    }*/

    public ForecastedParameterDTO deleteParameter(String name) {
        LOGGER.info("Deleting parameter with name: " + name);

        Optional<ForecastedParameter> p = dao.delete(name);
        if (p.isPresent()) {
            ForecastedParameter deletedParameter = p.get();
            ForecastedParameterDTO deletedParameterDTO = mapper.toDto(deletedParameter);

            return deletedParameterDTO;
        }
        throw new WebApplicationException("Parameter with name " + name + " not found", Response.Status.NOT_FOUND);
    }
}
