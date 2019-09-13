package com.opinta.service;

import com.opinta.dao.TariffGridDao;
import com.opinta.entity.TariffGrid;
import com.opinta.entity.W2wVariation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class TariffGridServiceImpl implements TariffGridService {
    private TariffGridDao tariffGridDao;

    @Autowired
    public TariffGridServiceImpl(TariffGridDao tariffGridDao) {
        this.tariffGridDao = tariffGridDao;
    }

    @Override
    @Transactional
    public List<TariffGrid> getAll() {
        log.info("Getting all tariffGrids");
        return tariffGridDao.getAll();
    }

    @Override
    @Transactional
    public TariffGrid getById(long id) {
        log.info("Getting tariffGrid by id {}", id);
        return tariffGridDao.getById(id);
    }

    @Override
    @Transactional
    public TariffGrid save(TariffGrid tariffGrid) {
        log.info("Saving tariffGrid {}", tariffGrid);
        return tariffGridDao.save(tariffGrid);
    }

    @Override
    @Transactional
    public TariffGrid update(long id, TariffGrid source) {
        TariffGrid target = tariffGridDao.getById(id);
        if (target == null) {
            log.info("Can't update tariffGrid. TariffGrid doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for tariffGrid", e);
        }
        target.setId(id);
        log.info("Updating tariffGrid {}", target);
        tariffGridDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        TariffGrid tariffGrid = tariffGridDao.getById(id);
        if (tariffGrid == null) {
            log.debug("Can't delete tariffGrid. TariffGrid doesn't exist {}", id);
            return false;
        }
        log.info("Deleting tariffGrid {}", tariffGrid);
        tariffGridDao.delete(tariffGrid);
        return true;
    }

    @Override
    @Transactional
    public TariffGrid getByDimension(float weight, float length, W2wVariation w2wVariation) {
        return tariffGridDao.getByDimension(weight, length, w2wVariation);
    }

    @Override
    @Transactional
    public TariffGrid getLast(W2wVariation w2wVariation) {
        return tariffGridDao.getLast(w2wVariation);
    }
}
