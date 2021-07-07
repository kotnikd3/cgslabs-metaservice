package com.cgs.jt.rwis.metaservice.core;

import com.cgs.jt.rwis.metaservice.api.ModelDTO;
import com.cgs.jt.rwis.metaservice.core.mappers.ModelMapper;
import com.cgs.jt.rwis.metaservice.db.dao.ModelDAO;
import com.cgs.jt.rwis.metaservice.db.entity.Model;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class ModelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelService.class);
    private static ModelMapper mapper = Mappers.getMapper(ModelMapper.class);

    @Inject
    private ModelDAO dao;

    public ModelDTO createModel(ModelDTO modelDTO) {
        LOGGER.info("Creating model: " + modelDTO.toString());

        Model model = mapper.fromDto(modelDTO);
        Model createdModel = dao.create(model);
        ModelDTO createdModelDTO = mapper.toDto(createdModel);

        return createdModelDTO;
    }

    public List<ModelDTO> getModels() {
        LOGGER.info("Getting all models.");

        List<Model> modelList = dao.findAll();
        List<ModelDTO> modelDTOList = mapper.toDtoList(modelList);

        return modelDTOList;
    }

    public ModelDTO getModel(String name) {
        LOGGER.info("Getting model with name: " + name);

        Optional<Model> m = dao.find(name);
        if (m.isPresent()) {
            Model model = m.get();
            ModelDTO modelDTO = mapper.toDto(model);
            return modelDTO;
        }
        throw new WebApplicationException("Model with name " + name + " not found", Response.Status.NOT_FOUND);
    }

    /*public ModelDTO updateModel(String name, ModelDTO modelDTO) {
        LOGGER.info("Updating model with name: " + name + ". New data: " + modelDTO.toString());

        Model model = mapper.fromDto(modelDTO);

        Optional<Model> m = dao.update(name, model);
        if (m.isPresent()) {
            Model updatedModel = m.get();
            ModelDTO updatedModelDTO = mapper.toDto(updatedModel);

            LOGGER.info("Updated model:" + updatedModelDTO.toString() + " Persisted model: " + model.toString());

            return updatedModelDTO;
        }
        throw new WebApplicationException("Model with name " + name + " not found", Response.Status.NOT_FOUND);
    }*/

    public ModelDTO deleteModel(String name) {
        LOGGER.info("Deleting model with name: " + name);

        Optional<Model> m = dao.delete(name);
        if (m.isPresent()) {
            Model deletedModel = m.get();
            ModelDTO deletedModelDTO = mapper.toDto(deletedModel);

            return deletedModelDTO;
        }
        throw new WebApplicationException("Model with name " + name + " not found", Response.Status.NOT_FOUND);
    }
}
