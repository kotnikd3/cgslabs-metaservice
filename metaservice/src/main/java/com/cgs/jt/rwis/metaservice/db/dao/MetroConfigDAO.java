package com.cgs.jt.rwis.metaservice.db.dao;

import com.cgs.jt.rwis.metaservice.db.entity.MetroConfig;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class MetroConfigDAO extends AbstractDAO<MetroConfig> {
    public MetroConfigDAO(SessionFactory factory) {
        super(factory);
    }

    public MetroConfig create(MetroConfig metroConfig) {
        MetroConfig metroConfigCreated = persist(metroConfig);
        return metroConfigCreated;
    }

    @SuppressWarnings("unchecked")
    public List<MetroConfig> findAll() {
        Query query = namedQuery("com.cgs.jt.rwis.metaservice.db.entity.MetroConfig.getAll");
        List<MetroConfig> metroConfigList = (List<MetroConfig>)query.getResultList();
        return metroConfigList;
    }

    public Optional<MetroConfig> find(MetroConfig.MetroConfigId id) {
        MetroConfig metroConfigPersisted = get(id);
        return Optional.ofNullable(metroConfigPersisted);
    }

    /*public Optional<Model> update(String name, Model model) {
        Model persistedModel = get(name);
        if (persistedModel != null) {
            Session currentSession = currentSession();

            model.setName(persistedModel.getName());
            Model updatedModel = (Model)currentSession.merge(model);
            return Optional.of(updatedModel);
        }
        return Optional.empty();
    }*/

    public Optional<MetroConfig> delete(MetroConfig.MetroConfigId id) {
        MetroConfig metroConfigPersisted = get(id);

        if (metroConfigPersisted != null) {
            Session currentSession = currentSession();
            currentSession.delete(metroConfigPersisted);
            return Optional.of(metroConfigPersisted);
        }
        return Optional.empty();
    }

}
