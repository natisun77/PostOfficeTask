package com.opinta.service;

import com.opinta.dao.PostOfficeDao;
import com.opinta.dto.PostOfficeDto;
import com.opinta.mapper.PostOfficeMapper;
import com.opinta.entity.PostOffice;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

@Service
@Slf4j
public class PostOfficeServiceImpl implements PostOfficeService {
    private PostOfficeDao postOfficeDao;
    private PostOfficeMapper postOfficeMapper;

    @Autowired
    public PostOfficeServiceImpl(PostOfficeDao postOfficeDao, PostOfficeMapper postOfficeMapper) {
        this.postOfficeDao = postOfficeDao;
        this.postOfficeMapper = postOfficeMapper;
    }

    @Override
    @Transactional
    public List<PostOffice> getAllEntities() {
        log.info("Getting all post offices");
        return postOfficeDao.getAll();
    }

    @Override
    @Transactional
    public PostOffice getEntityById(long id) {
        log.info("Getting client by id {}", id);
        return postOfficeDao.getById(id);
    }

    @Override
    @Transactional
    public PostOffice saveEntity(PostOffice postOffice) {
        log.info("Saving client {}", postOffice);
        return postOfficeDao.save(postOffice);
    }

    @Override
    @Transactional
    public List<PostOfficeDto> getAll() {
        return postOfficeMapper.toDto(getAllEntities());
    }

    @Override
    @Transactional
    public PostOfficeDto getById(long id) {
        return postOfficeMapper.toDto(getEntityById(id));
    }

    @Override
    @Transactional
    public PostOfficeDto save(PostOfficeDto postOfficeDto) {
        return postOfficeMapper.toDto(saveEntity(postOfficeMapper.toEntity(postOfficeDto)));
    }

    @Override
    @Transactional
    public PostOfficeDto update(long id, PostOfficeDto postOfficeDto) {
        PostOffice source = postOfficeMapper.toEntity(postOfficeDto);
        PostOffice target = postOfficeDao.getById(id);
        if (target == null) {
            log.info("Can't update postOffice. PostOffice doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for postOffice", e);
        }
        target.setId(id);
        log.info("Updating postOffice {}", target);
        postOfficeDao.update(target);
        return postOfficeMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        PostOffice postOffice = postOfficeDao.getById(id);
        if (postOffice == null) {
            log.debug("Can't delete postOffice. PostOffice doesn't exist {}", id);
            return false;
        }
        log.info("Deleting postOffice {}", postOffice);
        postOfficeDao.delete(postOffice);
        return true;
    }
}
