package com.opinta.dao;

import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParcelDaoImpl implements ParcelDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public ParcelDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Parcel> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Parcel.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Parcel> getAllByShipment(Shipment shipment) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Parcel.class)
                .add(Restrictions.eq("shipment", shipment))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    public Parcel getById(long id) {
        Session session = sessionFactory.getCurrentSession();
        return (Parcel) session.get(Parcel.class, id);
    }

    @Override
    public Parcel save(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        return (Parcel) session.merge(parcel);
    }

    @Override
    public void update(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        session.update(parcel);
    }

    @Override
    public void delete(Parcel parcel) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(parcel);
    }
}
