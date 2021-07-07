package com.cgs.jt.rwis.metaservice.db.dao;

import com.cgs.jt.rwis.metaservice.db.entity.MeasuredParameter;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class MeasuredParameterDAO extends AbstractDAO<MeasuredParameter> {
    public MeasuredParameterDAO(SessionFactory factory) {
        super(factory);
    }

    public MeasuredParameter create(MeasuredParameter parameter) {
        MeasuredParameter createdParameter = persist(parameter);
        return createdParameter;
    }

    @SuppressWarnings("unchecked")
    public List<MeasuredParameter> findAll() {
        Query query = namedQuery("com.cgs.jt.rwis.metaservice.db.entity.MeasuredParameter.getAll");
        List<MeasuredParameter> parameterList = (List<MeasuredParameter>)query.getResultList();
        return parameterList;
    }

    public Optional<MeasuredParameter> findByName(String name) {
        MeasuredParameter persistedParameter = get(name);
        return Optional.ofNullable(persistedParameter);
    }

    /*public Optional<Parameter> update(String name, Parameter parameter) {
        Parameter persistedParameter = get(name);
        if (persistedParameter != null) {
            Session currentSession = currentSession();

            parameter.setName(persistedParameter.getName());
            Parameter updatedParameter = (Parameter)currentSession.merge(parameter);
            return Optional.of(updatedParameter);
        }
        return Optional.empty();
    }*/

    public Optional<MeasuredParameter> delete(String name) {
        MeasuredParameter persistedParameter = get(name);

        if (persistedParameter != null) {
            Session currentSession = currentSession();
            currentSession.delete(persistedParameter);
            return Optional.of(persistedParameter);
        }
        return Optional.empty();
    }

}
