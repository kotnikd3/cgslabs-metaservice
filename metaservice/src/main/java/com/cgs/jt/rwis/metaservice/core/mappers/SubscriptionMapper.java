package com.cgs.jt.rwis.metaservice.core.mappers;
import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.cgs.jt.rwis.api.GeographicLocation;
import com.cgs.jt.rwis.api.ParameterForecastSubscription;
import com.cgs.jt.rwis.api.params.ForecastedParameter;
import com.cgs.jt.rwis.metaservice.db.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SubscriptionMapper {

    @Mapping(source = "parameter.label", target = "forecastedParameter.name")
    @Mapping(source = "forecastModelId", target = "model.name")
    @Mapping(source = "customerId", target = "customerId")
    @Mapping(source = "subscriptionPoint.elevation", target = "elevation")
    @Mapping(source = "subscriptionPoint.geoLocation.latitude", target = "location.latitude")
    @Mapping(source = "subscriptionPoint.geoLocation.longitude", target = "location.longitude")
    Subscription toSubscription(ParameterForecastSubscription subscription);

    List<ParameterForecastSubscription> toParameterForecastSubscriptionList(List<Subscription> subscriptionList);

    default EarthSurfacePoint toEarthSurfacePoint(Subscription subscription) {
        return new EarthSurfacePoint(new GeographicLocation(subscription.getLocation().getLatitude(), subscription.getLocation().getLongitude()), subscription.getElevation());
    }

    default ParameterForecastSubscription toParameterForecastSubscription(Subscription subscription) {
        return new ParameterForecastSubscription(ForecastedParameter.get(subscription.getForecastedParameter().getName()),
                new EarthSurfacePoint(new GeographicLocation(subscription.getLocation().getLatitude(), subscription.getLocation().getLongitude()), subscription.getElevation()),
                subscription.getModel().getName(),
                subscription.getCustomerId());
    }
}
