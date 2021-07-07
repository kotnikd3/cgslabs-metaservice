package com.cgs.jt.rwis.metaservice.db.dao;

import com.cgs.jt.rwis.metaservice.db.entity.Subscription;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class SubscriptionDAO extends AbstractDAO<Subscription> {
    public SubscriptionDAO(SessionFactory factory) {
        super(factory);
    }

    public Subscription create(Subscription subscription) {
        Subscription createdSubscription = persist(subscription);
        return createdSubscription;
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> findAll() {
//        return list((Query<Subscription>) namedQuery("com.cgs.jt.rwis.metaservice.db.entity.Subscription.getAll"));
        Query query = namedQuery("com.cgs.jt.rwis.metaservice.db.entity.Subscription.getAll");
        List<Subscription> subscriptionList = (List<Subscription>)query.getResultList();
        return subscriptionList;
    }

    public List<Subscription> findAllByModelName(String modelName) {
        Query query = namedQuery("com.cgs.jt.rwis.metaservice.db.entity.Subscription.getAllByModelName");
        query.setParameter("model_name", modelName);
        List<Subscription> subscriptionList = (List<Subscription>)query.getResultList();
        return subscriptionList;
    }

    public List<Subscription> findAllByModelNameAndLocation(String modelName, Double latitude, Double longitude) {
        Query query = namedQuery("com.cgs.jt.rwis.metaservice.db.entity.Subscription.getAllByModelNameAndLocation");
        query.setParameter("model_name", modelName);
        query.setParameter("latitude", latitude);
        query.setParameter("longitude", longitude);
        List<Subscription> subscriptionList = (List<Subscription>)query.getResultList();
        return subscriptionList;
    }

    /*public Optional<Subscription> update(int id, Subscription subscription) {
        Subscription persistedSubscription = get(id);
        if (persistedSubscription != null) {
            Session currentSession = currentSession();
            Subscription updatedModel = (Subscription) currentSession.merge(subscription);
            return Optional.of(updatedModel);
        }
        return Optional.empty();
    }*/

    public Optional<Subscription> delete(Subscription.SubscriptionId id) {
        Subscription persistedSubscription = get(id);

        if (persistedSubscription != null) {
            Session currentSession = currentSession();
            currentSession.delete(persistedSubscription);
            return Optional.of(persistedSubscription);
        }
        return Optional.empty();
    }

}
