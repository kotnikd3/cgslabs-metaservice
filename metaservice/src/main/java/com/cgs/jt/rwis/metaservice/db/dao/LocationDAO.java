package com.cgs.jt.rwis.metaservice.db.dao;

import com.cgs.jt.rwis.metaservice.db.entity.Location;
import com.cgs.jt.rwis.metaservice.db.entity.Subscription;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class LocationDAO extends AbstractDAO<Location> {
    public LocationDAO(SessionFactory factory) {
        super(factory);
    }

    public Location create(Location location) {
        Location createdLocation = persist(location);
        return createdLocation;
    }

    public Optional<Location> find(Location.LocationId id) {
        Location location = get(id);
        return Optional.ofNullable(location);
    }

    public Optional<Location> delete(Location.LocationId id) {
        Location persistedLocation = get(id);

        if (persistedLocation != null) {
            Session currentSession = currentSession();
            currentSession.delete(persistedLocation);
            return Optional.of(persistedLocation);
        }
        return Optional.empty();
    }
}
