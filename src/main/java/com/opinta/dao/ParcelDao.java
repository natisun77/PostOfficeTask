package com.opinta.dao;

import com.opinta.entity.Parcel;
import com.opinta.entity.Shipment;

import java.util.List;

public interface ParcelDao {

    List<Parcel> getAll();

    List<Parcel> getAllByShipment(Shipment shipment);

    Parcel getById(long id);

    Parcel save(Parcel parcel);

    void update(Parcel parcel);

    void delete(Parcel parcel);
}
