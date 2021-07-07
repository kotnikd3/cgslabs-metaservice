package com.cgs.jt.rwis.metaservice.core;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.cgs.jt.rwis.api.ParameterForecastSubscription;
import com.cgs.jt.rwis.metaservice.core.mappers.SubscriptionMapper;
import com.cgs.jt.rwis.metaservice.db.dao.LocationDAO;
import com.cgs.jt.rwis.metaservice.db.dao.SubscriptionDAO;
import com.cgs.jt.rwis.metaservice.db.entity.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

public class SubscriptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionService.class);
    private static SubscriptionMapper mapper = Mappers.getMapper(SubscriptionMapper.class);

    @Inject
    private SubscriptionDAO subscriptionDAO;

    @Inject
    private LocationDAO locationDAO;

    public ParameterForecastSubscription createSubscription(ParameterForecastSubscription parameterForecastSubscription) {
        LOGGER.info("Creating subscription: " + parameterForecastSubscription.toString());

        Subscription subscription = mapper.toSubscription(parameterForecastSubscription);

        // We need to create (and persist) Location.
        Location location = new Location();
        location.setLatitude(subscription.getLocation().getLatitude());
        location.setLongitude(subscription.getLocation().getLongitude());
        locationDAO.create(location);
        subscription.setLocation(location);

        Subscription createdSubscription = subscriptionDAO.create(subscription);
        ParameterForecastSubscription subscriptionJernejAnswer = mapper.toParameterForecastSubscription(createdSubscription);

        return subscriptionJernejAnswer;
    }


    public List<ParameterForecastSubscription> getSubscriptions() {
        LOGGER.info("Getting all subscriptions.");

        List<Subscription> subscriptionList = subscriptionDAO.findAll();
        List<ParameterForecastSubscription> parameterForecastSubscriptionList = mapper.toParameterForecastSubscriptionList(subscriptionList);

        return parameterForecastSubscriptionList;
    }

    public HashMap<EarthSurfacePoint, HashMap<String, HashSet<String>>> getSubscriptionsByModelName(String modelName) {
        LOGGER.info("Getting subscriptions for model name: " + modelName);

        List<Subscription> subscriptionList = subscriptionDAO.findAllByModelName(modelName);

        // Custom mapping - grouping by location
        HashMap<EarthSurfacePoint, HashMap<String, HashSet<String>>> customMap = new HashMap<>();
        for (Subscription s : subscriptionList) {
            EarthSurfacePoint earthSurfacePointDTO = mapper.toEarthSurfacePoint(s);

            if (!customMap.containsKey(earthSurfacePointDTO)) {
                customMap.put(earthSurfacePointDTO, new HashMap<>());
            }
            if (!customMap.get(earthSurfacePointDTO).containsKey(s.getForecastedParameter().getName())) {
                customMap.get(earthSurfacePointDTO).put(s.getForecastedParameter().getName(), new HashSet<>());
            }
            customMap.get(earthSurfacePointDTO).get(s.getForecastedParameter().getName()).add(s.getCustomerId());
        }

        return customMap;
    }

    public List<ParameterForecastSubscription> getSubscriptionsByModelNameAndLocation(String modelName, Double latitude, Double longitude) {
        LOGGER.info("Getting subscriptions for model name: " + modelName + " and latitude: " + latitude + ", longitude: " + longitude);

        List<Subscription> subscriptionList = subscriptionDAO.findAllByModelNameAndLocation(modelName, latitude, longitude);
        List<ParameterForecastSubscription> parameterForecastSubscriptionList = mapper.toParameterForecastSubscriptionList(subscriptionList);

        return parameterForecastSubscriptionList;
    }

    /*public SubscriptionDTO updateSubscription(int id, SubscriptionDTO subscriptionDTO) {
        LOGGER.info("Updating subscription with ID: " + id + ". New data: " + subscriptionDTO.toString());

        Subscription subscription = mapper.fromDto(subscriptionDTO);

        Optional<Subscription> s = subscriptionDAO.update(id, subscription);
        if (s.isPresent()) {
            Subscription updatedSubscription = s.get();
            SubscriptionDTO updatedSubscriptionDTO = mapper.toDto(updatedSubscription);

            return updatedSubscriptionDTO;
        }
        throw new WebApplicationException("Subscription with ID " + id + " not found", Response.Status.NOT_FOUND);
    }*/

    public ParameterForecastSubscription deleteSubscription(String modelName, Double latitude, Double longitude, String customer, String parameterName) {
        LOGGER.info("Deleting subscription for model: " + modelName + " latitude: " + latitude + " longitude: " + longitude +
                " customer: " + customer + " forecasted parameter: " + parameterName);

        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        Model model = new Model();
        model.setName(modelName);
        ForecastedParameter parameter = new ForecastedParameter();
        parameter.setName(parameterName);

        Optional<Subscription> s = subscriptionDAO.delete(new Subscription.SubscriptionId(location, model, parameter, customer));

        if (s.isPresent()) {
            Subscription deletedSubscription = s.get();

            // If location does not have any Subscription or MetroConfig, delete it.
            List<Subscription> subscriptionList = deletedSubscription.getLocation().getSubscriptionList();
            subscriptionList.remove(deletedSubscription);
            List<MetroConfig> metroConfigList = deletedSubscription.getLocation().getMetroConfigList();
            metroConfigList.remove(deletedSubscription);
            if (subscriptionList.isEmpty() && metroConfigList.isEmpty())
                locationDAO.delete(new Location.LocationId(deletedSubscription.getLocation().getLatitude(), deletedSubscription.getLocation().getLongitude()));

            ParameterForecastSubscription deletedParameterForecastSubscription = mapper.toParameterForecastSubscription(deletedSubscription);
            return deletedParameterForecastSubscription;
        }
        throw new WebApplicationException("Subscription for model: " + modelName + " latitude: " + latitude + " longitude: " + longitude +
                " customer: " + customer + " forecasted parameter: " + parameterName + " not found", Response.Status.NOT_FOUND);
    }
}

