package com.cgs.jt.rwis.metaservice.core;

import com.cgs.jt.rwis.metaservice.api.MeasuredParameterDTO;
import com.cgs.jt.rwis.metaservice.core.mappers.MeasuredParameterMapper;
import com.cgs.jt.rwis.metaservice.db.dao.MeasuredParameterDAO;
import com.cgs.jt.rwis.metaservice.db.entity.MeasuredParameter;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class MeasuredParameterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasuredParameterService.class);
    private static MeasuredParameterMapper mapper = Mappers.getMapper(MeasuredParameterMapper.class);

    @Inject
    private MeasuredParameterDAO dao;

    public MeasuredParameterDTO createParameter(MeasuredParameterDTO parameterDTO) {
        LOGGER.info("Creating parameter: " + parameterDTO.toString());

        MeasuredParameter parameter = mapper.fromDto(parameterDTO);
        MeasuredParameter createdParameter = dao.create(parameter);
        MeasuredParameterDTO createdParameterDTO = mapper.toDto(createdParameter);

        return createdParameterDTO;
    }

    public List<MeasuredParameterDTO> getParameters() {
        LOGGER.info("Getting all parameters.");

        List<MeasuredParameter> parameterList = dao.findAll();
        List<MeasuredParameterDTO> parameterDTOList = mapper.toDtoList(parameterList);

        return parameterDTOList;
    }

    public MeasuredParameterDTO getParameter(String name) {
        LOGGER.info("Getting parameter with name: " + name);

        Optional<MeasuredParameter> p = dao.findByName(name);
        if (p.isPresent()) {
            MeasuredParameter parameter = p.get();
            MeasuredParameterDTO parameterDTO = mapper.toDto(parameter);

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

    public MeasuredParameterDTO deleteParameter(String name) {
        LOGGER.info("Deleting parameter with name: " + name);

        Optional<MeasuredParameter> p = dao.delete(name);
        if (p.isPresent()) {
            MeasuredParameter deletedParameter = p.get();
            MeasuredParameterDTO deletedParameterDTO = mapper.toDto(deletedParameter);

            return deletedParameterDTO;
        }
        throw new WebApplicationException("Parameter with name " + name + " not found", Response.Status.NOT_FOUND);
    }
}

