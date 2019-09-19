package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ClientDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.ClientService;
import com.opinta.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final ShipmentService shipmentService;
    private final String CLIENT_DOES_NOT_EXIST = "No client found for ID %d";
    
    @Autowired
    public ClientController(ClientService clientService, ShipmentService shipmentService) {
        this.clientService = clientService;
        this.shipmentService = shipmentService;
    }
    
    @GetMapping
    @ResponseStatus(OK)
    public List<ClientDto> getAllClients() {
        return this.clientService.getAll();
    }
    
    @GetMapping("{id}")
    public ResponseEntity<?> getClient(@PathVariable("id") long id) {
        ClientDto clientDto = clientService.getById(id);
        if (clientDto == null) {
            return new ResponseEntity<>(format(CLIENT_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(clientDto, OK);
    }

    @GetMapping("{clientId}/shipments")
    public ResponseEntity<?> getShipmentsByClientId(@PathVariable long clientId) {
        List<ShipmentDto> shipmentDtos = shipmentService.getAllByClientId(clientId);
        if (shipmentDtos == null) {
            return new ResponseEntity<>(format(CLIENT_DOES_NOT_EXIST, clientId), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDtos, OK);
    }
    
    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody ClientDto clientDto) {
        clientDto = clientService.save(clientDto);
        if (clientDto == null) {
            return new ResponseEntity<>("New Client has not been saved", BAD_REQUEST);
        }
        return new ResponseEntity<>(clientDto, OK);
    }
    
    @PutMapping("{id}")
    public ResponseEntity<?> updateClient(@PathVariable long id, @RequestBody ClientDto clientDto) {
        clientDto = clientService.update(id, clientDto);
        if (clientDto != null) {
            return new ResponseEntity<>(clientDto, OK);
        } else {
            return new ResponseEntity<>(format(CLIENT_DOES_NOT_EXIST, id), NOT_FOUND);
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteClient(@PathVariable long id) {
        if (!clientService.delete(id)) {
            return new ResponseEntity<>(format(CLIENT_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
