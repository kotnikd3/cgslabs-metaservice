package com.cgs.jt.rwis.metaservice.db.dao;

import com.cgs.jt.rwis.metaservice.db.entity.Station;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class StationDAO extends AbstractDAO<Station> {
    public StationDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Station create(Station station) {
        Station createdStation = persist(station);
        return createdStation;
    }

    @SuppressWarnings("unchecked")
    public List<Station> findAll() {
        return list((Query<Station>) namedQuery("com.cgs.jt.rwis.metaservice.db.entity.Station.getAll"));
    }

    public Optional<Station> find(int id) {
        Station persistedStation = get(id);
        return Optional.ofNullable(persistedStation);
    }

    public Optional<Station> update(int id, Station station) {
        Station persistedStation = get(id);
        if (persistedStation != null) {
            Session currentSession = currentSession();

            station.setId(persistedStation.getId());
            Station updatedStation = (Station)currentSession.merge(station);
            return Optional.of(updatedStation);
        }
        return Optional.empty();
    }

    public Optional<Station> delete(int id) {
        Station persistedStation = get(id);
        if (persistedStation != null) {
            Session currentSession = currentSession();
            currentSession.delete(persistedStation);
            return Optional.of(persistedStation);
        }
        return Optional.empty();
    }
}
