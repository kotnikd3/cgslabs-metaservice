package com.cgs.jt.rwis.metaservice.core;

import com.cgs.jt.rwis.metaservice.core.mappers.MetroConfigMapper;
import com.cgs.jt.rwis.metaservice.db.dao.LocationDAO;
import com.cgs.jt.rwis.metaservice.db.dao.MetroConfigDAO;
import com.cgs.jt.rwis.metaservice.db.entity.Location;
import com.cgs.jt.rwis.metaservice.db.entity.MetroConfig;
import com.cgs.jt.rwis.metaservice.db.entity.Model;
import com.cgs.jt.rwis.metaservice.db.entity.Subscription;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class MetroConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetroConfigService.class);
    private static MetroConfigMapper mapper = Mappers.getMapper(MetroConfigMapper.class);

    @Inject
    private MetroConfigDAO metroConfigDAO;

    @Inject
    private LocationDAO locationDAO;

    public MetroLocationDescription createMetroConfig(MetroLocationDescription metroLocationDescription) {
        LOGGER.info("Creating MetroLocationDescription: " + metroLocationDescription.toString());

        try {
            MetroConfig metroConfig = mapper.toMetroConfig(metroLocationDescription);

            // We need to create (and persist) Location.
            Location location = new Location();
            location.setLatitude(metroConfig.getLocation().getLatitude());
            location.setLongitude(metroConfig.getLocation().getLongitude());
            locationDAO.create(location);
            metroConfig.setLocation(location);

            MetroConfig createdMetroConfig = metroConfigDAO.create(metroConfig);

            MetroLocationDescription createdMetroLocationDescription = mapper.toMetroLocationDescription(createdMetroConfig);
            return createdMetroLocationDescription;
        } catch (JsonProcessingException e) {
            throw new WebApplicationException("Error parsing JSON: " + e.toString(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public List<MetroLocationDescription> getMetroConfigs() {
        LOGGER.info("Getting all metroConfigs.");

        List<MetroConfig> metroConfigList = metroConfigDAO.findAll();
        List<MetroLocationDescription> metroLocationDescriptionList = mapper.toMetroLocationDescriptionList(metroConfigList);

        return metroLocationDescriptionList;
    }

    public MetroLocationDescription getMetroConfigForModelAndLocation(String modelName, double latitude, double longitude) {
        LOGGER.info("Getting metroConfig with latitude: " + latitude + " longitude: " + longitude + " model: " + modelName);

        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        Model model = new Model();
        model.setName(modelName);

        Optional<MetroConfig> mc = metroConfigDAO.find(new MetroConfig.MetroConfigId(location, model));

        if (mc.isPresent()) {
            MetroConfig metroConfig = mc.get();
            try {
                MetroLocationDescription metroLocationDescription = mapper.toMetroLocationDescription(metroConfig);
                return metroLocationDescription;
            } catch (JsonProcessingException e) {
                throw new WebApplicationException("Error parsing JSON: " + e.toString(), Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        throw new WebApplicationException("MetroConfig with latitude " + latitude + " and longitude " + longitude + " for model " + modelName + " not found", Response.Status.NOT_FOUND);
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

    public MetroLocationDescription deleteMetroConfigForModelAndLocation(String modelName, double latitude, double longitude) {
        LOGGER.info("Deleting metroConfig with latitude: " + latitude + " longitude: " + longitude + " model: " + modelName);

        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        Model model = new Model();
        model.setName(modelName);

        Optional<MetroConfig> mc = metroConfigDAO.delete(new MetroConfig.MetroConfigId(location, model));

        if (mc.isPresent()) {
            MetroConfig deletedMetroConfig = mc.get();

            // If location does not have any Subscription or MetroConfig, delete it.
            List<Subscription> subscriptionList = deletedMetroConfig.getLocation().getSubscriptionList();
            subscriptionList.remove(deletedMetroConfig);
            List<MetroConfig> metroConfigList = deletedMetroConfig.getLocation().getMetroConfigList();
            metroConfigList.remove(deletedMetroConfig);
            if (subscriptionList.isEmpty() && metroConfigList.isEmpty())
                locationDAO.delete(new Location.LocationId(deletedMetroConfig.getLocation().getLatitude(), deletedMetroConfig.getLocation().getLongitude()));

            try {
                MetroLocationDescription metroLocationDescription = mapper.toMetroLocationDescription(deletedMetroConfig);
                return metroLocationDescription;
            } catch (JsonProcessingException e) {
                throw new WebApplicationException("Error parsing JSON: " + e.toString(), Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        throw new WebApplicationException("MetroConfig with latitude " + latitude + " and longitude " + longitude + " for model " + modelName + " not found", Response.Status.NOT_FOUND);
    }
}
