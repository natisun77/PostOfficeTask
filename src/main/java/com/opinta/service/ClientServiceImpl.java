package com.opinta.service;

import com.opinta.dao.ClientDao;
import com.opinta.dao.CounterpartyDao;
import com.opinta.dto.ClientDto;
import com.opinta.entity.Client;
import com.opinta.entity.Counterparty;
import com.opinta.mapper.ClientMapper;
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
public class ClientServiceImpl implements ClientService {
    private final ClientDao clientDao;
    private final CounterpartyDao counterpartyDao;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao, ClientMapper clientMapper,
                             CounterpartyDao counterpartyDao) {
        this.clientDao = clientDao;
        this.clientMapper = clientMapper;
        this.counterpartyDao = counterpartyDao;
    }

    @Override
    @Transactional
    public List<Client> getAllEntities() {
        log.info("Getting all clients existed");
        return clientDao.getAll();
    }

    @Override
    @Transactional
    public Client getEntityById(long id) {
        log.info("Getting address by id {}", id);
        return clientDao.getById(id);
    }

    @Override
    @Transactional
    public Client saveEntity(Client client) {
        log.info("Saving address {}", client);
        return clientDao.save(client);
    }

    @Override
    @Transactional
    public List<ClientDto> getAll() {
        log.info("Getting all clients");
        List<Client> allClients = clientDao.getAll();
        return clientMapper.toDto(allClients);
    }

    @Override
    @Transactional
    public List<ClientDto> getAllByCounterpartyId(long counterpartyId) {
        Counterparty counterparty = counterpartyDao.getById(counterpartyId);
        if (counterparty == null) {
            log.debug("Can't get client list by counterparty. Counterparty {} doesn't exist", counterpartyId);
            return Collections.emptyList();
        }
        log.info("Getting all clients by counterparty {}", counterparty);
        return clientMapper.toDto(clientDao.getAllByCounterparty(counterparty));
    }

    @Override
    @Transactional
    public ClientDto getById(long id) {
        log.info("Getting client by id {}", id);
        Client client = clientDao.getById(id);
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto save(ClientDto clientDto) {
        log.info("Saving client {}", clientDto);
        Client client = clientMapper.toEntity(clientDto);
        client = clientDao.save(client);
        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto update(long id, ClientDto clientDto) {
        Client source = clientMapper.toEntity(clientDto);
        Client target = clientDao.getById(id);
        if (target == null) {
            log.debug("Can't update client. Client doesn't exist {}", id);
            return null;
        }
        try {
            copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't get properties from object to updatable object for client", e);
        }
        target.setId(id);
        log.info("Updating client {}", target);
        clientDao.update(target);
        return clientMapper.toDto(target);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        Client client = clientDao.getById(id);
        if (client == null) {
            log.debug("Can't delete client. Client doesn't exist " + id);
            return false;
        }
        log.info("Deleting client {}", client);
        clientDao.delete(client);
        return true;
    }
}
