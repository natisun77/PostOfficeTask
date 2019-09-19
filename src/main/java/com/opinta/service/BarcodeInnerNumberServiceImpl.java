package com.opinta.service;

import com.opinta.dao.BarcodeInnerNumberDao;
import com.opinta.dao.PostcodePoolDao;
import com.opinta.dto.BarcodeInnerNumberDto;
import com.opinta.entity.BarcodeInnerNumber;
import com.opinta.entity.PostcodePool;
import com.opinta.mapper.BarcodeInnerNumberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.opinta.entity.BarcodeStatus.USED;
import static java.lang.String.format;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class BarcodeInnerNumberServiceImpl implements BarcodeInnerNumberService {
    // TODO delete after implementation stored procedure that generates innerNumbers
    private static final Map<String, Integer> POSTCODE_COUNTERS = new HashMap<>();

    private final BarcodeInnerNumberDao barcodeInnerNumberDao;
    private final PostcodePoolDao postcodePoolDao;
    private final BarcodeInnerNumberMapper barcodeInnerNumberMapper;

    @Autowired
    public BarcodeInnerNumberServiceImpl(BarcodeInnerNumberDao barcodeInnerNumberDao,
                                         BarcodeInnerNumberMapper barcodeInnerNumberMapper,
                                         PostcodePoolDao postcodePoolDao) {
        this.barcodeInnerNumberDao = barcodeInnerNumberDao;
        this.barcodeInnerNumberMapper = barcodeInnerNumberMapper;
        this.postcodePoolDao = postcodePoolDao;
    }

    @Override
    @Transactional
    public List<BarcodeInnerNumberDto> getAll(long postcodeId) {
        PostcodePool postcodePool = postcodePoolDao.getById(postcodeId);
        if (postcodePool == null) {
            log.debug("Can't get barcodeInnerNumberDto list by postcodePool. PostCodePool {} " +
                    "doesn't exist", postcodeId);
            return Collections.emptyList();
        }
        log.info("Getting all barcodeInnerNumbers by postcodeId {}", postcodeId);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getAll(postcodeId));
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto getById(long id) {
        log.info("Getting barcodeInnerNumber by id {}", id);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberDao.getById(id));
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto save(long postcodeId, BarcodeInnerNumberDto barcodeInnerNumberDto) {
        PostcodePool postcodePool = postcodePoolDao.getById(postcodeId);
        if (postcodePool == null) {
            log.debug("Can't add barcodeInnerNumberDto to postcodePool. PostCodePool {} doesn't exist", postcodeId);
            return null;
        }
        BarcodeInnerNumber barcodeInnerNumber = barcodeInnerNumberMapper.toEntity(barcodeInnerNumberDto);
        BarcodeInnerNumber barcodeInnerNumberSaved = barcodeInnerNumberDao.save(barcodeInnerNumber);
        // TODO not to get, but set previously created list and check in db if previous values not erased
        postcodePool.getBarcodeInnerNumbers().add(barcodeInnerNumberSaved);
        log.info("Adding barcodeInnerNumber {} to postcodePool {}", barcodeInnerNumber, postcodePool);
        postcodePoolDao.update(postcodePool);
        return barcodeInnerNumberMapper.toDto(barcodeInnerNumberSaved);
    }

    @Override
    @Transactional
    public BarcodeInnerNumberDto update(long id, BarcodeInnerNumberDto barcodeInnerNumberDto) {
        BarcodeInnerNumber source = barcodeInnerNumberMapper.toEntity(barcodeInnerNumberDto);
        BarcodeInnerNumber target = barcodeInnerNumberDao.getById(id);
        if (target == null) {
            log.info("Can't update barcodeInnerNumber. BarcodeInnerNumber doesn't exist {}", id);
            return null;
        }

        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for barcodeInnerNumber", e);
        }
        target.setId(id);
        log.info("Updating barcodeInnerNumber {}", target);
        barcodeInnerNumberDao.update(target);
        return barcodeInnerNumberMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        BarcodeInnerNumber barcodeInnerNumber = barcodeInnerNumberDao.getById(id);
        if (barcodeInnerNumber == null) {
            log.debug("Can't delete barcodeInnerNumber. BarcodeInnerNumber doesn't exist {}", id);
            return false;
        }
        log.info("Deleting barcodeInnerNumber {}", barcodeInnerNumber);
        barcodeInnerNumberDao.delete(barcodeInnerNumber);
        return true;
    }

    @Override
    public BarcodeInnerNumber generateBarcodeInnerNumber(PostcodePool postcodePool) {
        BarcodeInnerNumber barcodeInnerNumber = new BarcodeInnerNumber();
        barcodeInnerNumber.setStatus(USED);
        barcodeInnerNumber.setNumber(getNextInnerNumber(postcodePool.getPostcode()));
        return barcodeInnerNumberDao.save(barcodeInnerNumber);
    }

    private String getNextInnerNumber(String postcode) {
        POSTCODE_COUNTERS.putIfAbsent(postcode, 0);
        int innerNumberCounter = POSTCODE_COUNTERS.get(postcode);
        POSTCODE_COUNTERS.put(postcode, innerNumberCounter + 1);
        if (innerNumberCounter > 9_999_999) {
            throw new RuntimeException(format("Barcode %d is too large", innerNumberCounter));
        }
        return String.format("%07d", innerNumberCounter);
    }
}
