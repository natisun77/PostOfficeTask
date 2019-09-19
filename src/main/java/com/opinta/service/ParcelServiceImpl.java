package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ShipmentDao;
import com.opinta.dao.TariffGridDao;
import com.opinta.dto.ParcelDto;
import com.opinta.entity.Address;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;
import com.opinta.mapper.ParcelMapper;
import com.opinta.util.AddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelServiceImpl implements ParcelService {
    private final ParcelDao parcelDao;
    private final ShipmentDao shipmentDao;
    private final TariffGridDao tariffGridDao;
    private final ParcelMapper parcelMapper;

    @Autowired
    public ParcelServiceImpl(ParcelDao parcelDao, ShipmentDao shipmentDao,
                             TariffGridDao tariffGridDao, ParcelMapper parcelMapper) {
        this.parcelDao = parcelDao;
        this.shipmentDao = shipmentDao;
        this.tariffGridDao = tariffGridDao;
        this.parcelMapper = parcelMapper;
    }

    @Override
    @Transactional
    public List<Parcel> getAllEntities() {
        log.info("Getting all parcels");
        return parcelDao.getAll();
    }

    @Override
    @Transactional
    public Parcel getEntityById(long id) {
        log.info("Getting parcel by id {}", id);
        return parcelDao.getById(id);
    }

    @Override
    @Transactional
    public Parcel saveEntity(Parcel parcel) {
        log.info("Saving parcel {}", parcel);
        return parcelDao.save(parcel);
    }

    @Override
    @Transactional
    public List<ParcelDto> getAll() {
        return parcelMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public List<ParcelDto> getAllByShipmentId(long shipmentId) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if (shipment == null) {
            log.debug("Can't get parcels list by shipment. Shipment {} doesn't exist", shipmentId);
            return Collections.emptyList();
        }
        log.info("Getting all parcels by shipment {}", shipment);
        return parcelMapper.toDto(parcelDao.getAllByShipment(shipment));
    }

    @Override
    @Transactional
    public ParcelDto getById(long id) {
        return parcelMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelDto save(long shipmentId, ParcelDto parcelDto) {
        Shipment shipment = shipmentDao.getById(shipmentId);
        if (shipment == null) {
            log.debug("Can't add parcel to shipment. Shipment {} doesn't exist", shipmentId);
            return null;
        }
        Parcel parcel = parcelMapper.toEntity(parcelDto);
        parcel.setShipment(shipment);
        parcel.setPrice(calculateParcelPrice(parcel));

        Parcel parcelSaved = parcelDao.save(parcel);

        shipment.setPrice(calculateShipmentPrice(shipment));

        log.info("Adding parcel {} to shipment {}", parcel, shipment);
        shipmentDao.update(shipment);
        return parcelMapper.toDto(parcelSaved);
    }

    @Override
    @Transactional
    public ParcelDto update(long id, ParcelDto parcelDto) {
        Parcel source = parcelMapper.toEntity(parcelDto);
        Parcel target = parcelDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcel. Parcel doesn't exist {}", id);
            return null;
        }
        Shipment shipment = target.getShipment();
        List<ParcelItem> parcelItems = target.getParcelItems();
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for parcel", e);
        }
        target.setId(id);
        target.setShipment(shipment);
        target.setParcelItems(parcelItems);
        target.setPrice(calculateParcelPrice(target));

        log.info("Updating parcel {}", target);
        parcelDao.update(target);

        shipment.setPrice(calculateShipmentPrice(shipment));
        shipmentDao.update(shipment);
        return parcelMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Parcel parcel = parcelDao.getById(id);
        if (parcel == null) {
            log.debug("Can't delete parcel. Parcel doesn't exist {}", id);
            return false;
        }
        Shipment shipment = parcel.getShipment();
        shipment.getParcelList().removeIf((Parcel p) -> p.getId() == id);
        shipment.setPrice(calculateShipmentPrice(shipment));

        shipmentDao.update(shipment);

        log.info("Deleting parcel {}", parcel);
        parcelDao.delete(parcel);

        return true;
    }

    private BigDecimal calculateParcelPrice(Parcel parcel) {
        log.info("Calculating price for parcel {}", parcel);
        Shipment shipment = parcel.getShipment();

        Address senderAddress = shipment.getSender().getAddress();
        Address recipientAddress = shipment.getRecipient().getAddress();
        W2wVariation w2wVariation = W2wVariation.COUNTRY;
        if (AddressUtil.isSameTown(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.TOWN;
        } else if (AddressUtil.isSameRegion(senderAddress, recipientAddress)) {
            w2wVariation = W2wVariation.REGION;
        }

        TariffGrid tariffGrid = tariffGridDao.getLast(w2wVariation);
        if (parcel.getWeight() < tariffGrid.getWeight() &&
                parcel.getLength() < tariffGrid.getLength()) {
            tariffGrid = tariffGridDao.getByDimension(parcel.getWeight(), parcel.getLength(), w2wVariation);
        }

        log.info("TariffGrid for weight {} per length {} and type {}: {}",
                parcel.getWeight(), parcel.getLength(), w2wVariation, tariffGrid);

        if (tariffGrid == null) {
            return BigDecimal.ZERO;
        }

        float price = tariffGrid.getPrice() + getSurcharges(shipment);

        return new BigDecimal(Float.toString(price));
    }

    private BigDecimal calculateShipmentPrice(Shipment shipment) {
        return shipment.getParcelList().stream().map(Parcel::getPrice).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private float getSurcharges(Shipment shipment) {
        float surcharges = 0;
        if (shipment.getDeliveryType().equals(DeliveryType.D2W) ||
                shipment.getDeliveryType().equals(DeliveryType.W2D)) {
            surcharges += 9;
        } else if (shipment.getDeliveryType().equals(DeliveryType.D2D)) {
            surcharges += 12;
        }
        return surcharges;
    }
}
