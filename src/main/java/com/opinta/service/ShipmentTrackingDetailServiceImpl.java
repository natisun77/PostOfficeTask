package com.opinta.service;

import com.opinta.dao.ShipmentTrackingDetailDao;
import com.opinta.dto.ShipmentTrackingDetailDto;
import com.opinta.entity.ShipmentTrackingDetail;
import com.opinta.mapper.ShipmentTrackingDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ShipmentTrackingDetailServiceImpl implements ShipmentTrackingDetailService {
    private ShipmentTrackingDetailDao shipmentTrackingDetailDao;
    private ShipmentTrackingDetailMapper shipmentTrackingDetailMapper;

    @Autowired
    public ShipmentTrackingDetailServiceImpl(ShipmentTrackingDetailDao shipmentTrackingDetailDao,
                                             ShipmentTrackingDetailMapper shipmentTrackingDetailMapper) {
        this.shipmentTrackingDetailDao = shipmentTrackingDetailDao;
        this.shipmentTrackingDetailMapper = shipmentTrackingDetailMapper;
    }

    @Override
    @Transactional
    public List<ShipmentTrackingDetailDto> getAll() {
        log.info("Getting all shipmentTrackingDetails");
        return shipmentTrackingDetailMapper.toDto(shipmentTrackingDetailDao.getAll());
    }

    @Override
    @Transactional
    public ShipmentTrackingDetailDto getById(long id) {
        log.info("Getting shipmentTrackingDetail by id {}", id);
        return shipmentTrackingDetailMapper.toDto(shipmentTrackingDetailDao.getById(id));
    }

    @Override
    @Transactional
    public ShipmentTrackingDetailDto save(ShipmentTrackingDetailDto shipmentTrackingDetailDto) {
        log.info("Saving shipmentTrackingDetail {}", shipmentTrackingDetailDto);
        return shipmentTrackingDetailMapper.toDto(
                shipmentTrackingDetailDao.save(
                        shipmentTrackingDetailMapper.toEntity(shipmentTrackingDetailDto)));
    }

    @Override
    @Transactional
    public ShipmentTrackingDetailDto update(long id, ShipmentTrackingDetailDto shipmentTrackingDetailDto) {
        ShipmentTrackingDetail source = shipmentTrackingDetailMapper.toEntity(shipmentTrackingDetailDto);
        ShipmentTrackingDetail target = shipmentTrackingDetailDao.getById(id);
        if (target == null) {
            log.info("Can't update shipmentTrackingDetail. ShipmentTrackingDetail doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for shipmentTrackingDetail", e);
        }
        target.setId(id);
        log.info("Updating shipmentTrackingDetail {}", target);
        shipmentTrackingDetailDao.update(target);
        return shipmentTrackingDetailMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        ShipmentTrackingDetail shipmentTrackingDetail = shipmentTrackingDetailDao.getById(id);
        if (shipmentTrackingDetail == null) {
            log.debug("Can't delete shipmentTrackingDetail. ShipmentTrackingDetail doesn't exist {}", id);
            return false;
        }
        log.info("Deleting shipmentTrackingDetail {}", shipmentTrackingDetail);
        shipmentTrackingDetailDao.delete(shipmentTrackingDetail);
        return true;
    }
}
