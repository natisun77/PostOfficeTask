package com.opinta.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.transaction.Transactional;

import com.opinta.dao.AddressDao;
import com.opinta.dto.AddressDto;
import com.opinta.mapper.AddressMapper;
import com.opinta.entity.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressDao addressDao;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressDao addressDao, AddressMapper addressMapper) {
        this.addressDao = addressDao;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public List<Address> getAllEntities() {
        log.info("Getting all addresses");
        return addressDao.getAll();
    }

    @Override
    @Transactional
    public Address getEntityById(long id) {
        log.info("Getting address by id {}", id);
        return addressDao.getById(id);
    }

    @Override
    @Transactional
    public Address saveEntity(Address address) {
        log.info("Saving address {}", address);
        return addressDao.save(address);
    }

    @Override
    @Transactional
    public Address updateEntity(long id, Address source) {
        Address target = addressDao.getById(id);
        if (target == null) {
            log.debug("Can't update address. Address doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for address", e);
        }
        target.setId(id);
        log.info("Updating address {}", target);
        addressDao.update(target);
        return target;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Address address = addressDao.getById(id);
        if (address == null) {
            log.debug("Can't delete address. Address doesn't exist {}", id);
            return false;
        }
        log.info("Deleting address {}", address);
        addressDao.delete(address);
        return true;
    }

    @Override
    @Transactional
    public List<AddressDto> getAll() {
        return addressMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public AddressDto getById(long id) {
        return addressMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public AddressDto save(AddressDto addressDto) {
        return addressMapper.toDto(saveEntity(addressMapper.toEntity(addressDto)));
    }

    @Override
    @Transactional
    public AddressDto update(long id, AddressDto addressDto) {
        Address address = updateEntity(id, addressMapper.toEntity(addressDto));
        return address == null ? null : addressMapper.toDto(address);
    }
}
