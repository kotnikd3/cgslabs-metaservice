package com.cgs.jt.rwis.metaservice.db.dao;

import com.cgs.jt.rwis.metaservice.db.entity.ForecastedParameter;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ForecastedParameterDAO extends AbstractDAO<ForecastedParameter>  {
    public ForecastedParameterDAO(SessionFactory factory) {
        super(factory);
    }

    public ForecastedParameter create(ForecastedParameter parameter) {
        ForecastedParameter createdParameter = persist(parameter);
        return createdParameter;
    }

    @SuppressWarnings("unchecked")
    public List<ForecastedParameter> findAll() {
        Query query = namedQuery("com.cgs.jt.rwis.metaservice.db.entity.ForecastedParameter.getAll");
        List<ForecastedParameter> parameterList = (List<ForecastedParameter>)query.getResultList();
        return parameterList;
    }

    public Optional<ForecastedParameter> findByName(String name) {
        ForecastedParameter persistedParameter = get(name);
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

    public Optional<ForecastedParameter> delete(String name) {
        ForecastedParameter persistedParameter = get(name);

        if (persistedParameter != null) {
            Session currentSession = currentSession();
            currentSession.delete(persistedParameter);
            return Optional.of(persistedParameter);
        }
        return Optional.empty();
    }

}
