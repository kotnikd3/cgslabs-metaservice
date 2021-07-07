package com.cgs.jt.rwis.metaservice.db.dao;

import com.cgs.jt.rwis.metaservice.db.entity.Model;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ModelDAO extends AbstractDAO<Model> {
    public ModelDAO(SessionFactory factory) {
        super(factory);
    }

    public Model create(Model model) {
        Model createdModel = persist(model);
        return createdModel;
    }

    @SuppressWarnings("unchecked")
    public List<Model> findAll() {
        Query query = namedQuery("com.cgs.jt.rwis.metaservice.db.entity.Model.getAll");
        List<Model> modelList = (List<Model>)query.getResultList();
        return modelList;
    }

    public Optional<Model> find(String name) {
        Model persistedModel = get(name);
        return Optional.ofNullable(persistedModel);
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

    public Optional<Model> delete(String name) {
        Model persistedModel = get(name);
        if (persistedModel != null) {
            Session currentSession = currentSession();
            currentSession.delete(persistedModel);
            return Optional.of(persistedModel);
        }
        return Optional.empty();
    }

}
