package com.opinta.service;

import com.opinta.dao.ParcelDao;
import com.opinta.dao.ParcelItemDao;
import com.opinta.dto.ParcelItemDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.mapper.ParcelItemMapper;
import com.opinta.mapper.ParcelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class ParcelItemServiceImpl implements ParcelItemService {
    private final ParcelItemDao parcelItemDao;
    private final ParcelItemMapper parcelItemMapper;
    private final ParcelDao parcelDao;
    private final ParcelMapper parcelMapper;

    @Autowired
    public ParcelItemServiceImpl(ParcelItemDao parcelItemDao, ParcelItemMapper parcelItemMapper,
                                 ParcelDao parcelDao, ParcelMapper parcelMapper) {
        this.parcelItemDao = parcelItemDao;
        this.parcelItemMapper = parcelItemMapper;
        this.parcelDao = parcelDao;
        this.parcelMapper = parcelMapper;
    }

    @Override
    @Transactional
    public List<ParcelItem> getAllEntities() {
        log.info("Getting all parcelItems");
        return parcelItemDao.getAll();
    }

    @Override
    @Transactional
    public ParcelItem getEntityById(long id) {
        log.info("Getting parcelItem by id {}", id);
        return parcelItemDao.getById(id);
    }

    @Override
    @Transactional
    public ParcelItem saveEntity(ParcelItem parcelItem) {
        log.info("Saving parcelItem {}", parcelItem);
        return parcelItemDao.save(parcelItem);
    }

    @Override
    @Transactional
    public ParcelItem updateEntity(long id, ParcelItem source) {
        ParcelItem target = parcelItemDao.getById(id);
        if (target == null) {
            log.debug("Can't update parcelItem. ParcelItem doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for parcelItem", e);
        }
        target.setId(id);
        log.info("Updating parcelItem {}", target);
        parcelItemDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        ParcelItem parcelItem = parcelItemDao.getById(id);
        if (parcelItem == null) {
            log.debug("Can't delete parcelItem. ParcelItem doesn't exist {}", id);
            return false;
        }
        log.info("Deleting parcelItem {}", parcelItem);
        parcelItemDao.delete(parcelItem);
        return true;
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAll() {
        return parcelItemMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public List<ParcelItemDto> getAllByParcelId(long parcelId) {
        Parcel parcel = parcelDao.getById(parcelId);
        if (parcel == null) {
            log.debug("Can't get parcelItems list by parcel. Parcel {} doesn't exist", parcelId);
            return Collections.emptyList();
        }
        log.info("Getting all parcelItems by parcel {}", parcel);
        return parcelItemMapper.toDto(parcelItemDao.getAllByParcel(parcel));
    }

    @Override
    @Transactional
    public ParcelItemDto getById(long id) {
        return parcelItemMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public ParcelItemDto save(long parcelId, ParcelItemDto parcelItemDto) {
        Parcel parcel = parcelDao.getById(parcelId);
        if (parcel == null) {
            log.debug("Can't add parcelItem to parcel. Parcel {} doesn't exist", parcelId);
            return null;
        }
        ParcelItem parcelItem = parcelItemMapper.toEntity(parcelItemDto);
        ParcelItem parcelItemSaved = parcelItemDao.save(parcelItem);

        parcel.getParcelItems().add(parcelItemSaved);

        log.info("Adding parcelItem {} to parcel {}", parcelItem, parcel);
        parcelDao.update(parcel);
        return parcelItemMapper.toDto(parcelItemSaved);
    }

    @Override
    @Transactional
    public ParcelItemDto update(long id, ParcelItemDto parcelItemDto) {
        ParcelItem parcelItem = updateEntity(id, parcelItemMapper.toEntity(parcelItemDto));
        return parcelItem == null ? null : parcelItemMapper.toDto(parcelItem);
    }
}
