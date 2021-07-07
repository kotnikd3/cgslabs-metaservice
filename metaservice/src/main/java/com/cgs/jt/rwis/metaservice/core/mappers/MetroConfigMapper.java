package com.cgs.jt.rwis.metaservice.core.mappers;

import com.cgs.jt.rwis.metaservice.db.entity.Location;
import com.cgs.jt.rwis.metaservice.db.entity.MetroConfig;
import com.cgs.jt.rwis.metaservice.db.entity.Model;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper
public interface MetroConfigMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    List<MetroLocationDescription> toMetroLocationDescriptionList(List<MetroConfig> metroConfigList);

    default MetroConfig toMetroConfig(MetroLocationDescription metroLocationDescription) throws JsonProcessingException {
        // Object -> JSON
        String config = objectMapper.writeValueAsString(metroLocationDescription);

        Location location = new Location();
        location.setLatitude(metroLocationDescription.getGeoLocation().getLatitude());
        location.setLongitude(metroLocationDescription.getGeoLocation().getLongitude());

        Model model = new Model();
        model.setName(metroLocationDescription.getForecastModelId());

        MetroConfig metroConfig = new MetroConfig();
        metroConfig.setLocation(location);
        metroConfig.setModel(model);
        metroConfig.setConfig(config);

        return metroConfig;
    }

    default MetroLocationDescription toMetroLocationDescription(MetroConfig metroConfig) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(metroConfig.getConfig());
        // JSON -> Object
        MetroLocationDescription metroLocationDescription = objectMapper.treeToValue(node, MetroLocationDescription.class);

        return metroLocationDescription;
    }
}
