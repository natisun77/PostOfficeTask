package com.opinta.controller;

import com.opinta.dto.AddressDto;
import com.opinta.service.AddressService;
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

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    private AddressService addressService;
    private final String ADDRESS_NOT_FOUND = "No Address found for ID %d";

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<AddressDto> getAddresses() {
        return addressService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAddress(@PathVariable("id") long id) {
        AddressDto addressDto = addressService.getById(id);
        if (addressDto == null) {
            return new ResponseEntity<>(format(ADDRESS_NOT_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(addressDto, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public ResponseEntity<?> createAddress(@RequestBody AddressDto addressDto) {
        addressDto = addressService.save(addressDto);
        if (addressDto == null) {
            return new ResponseEntity<>("Failed to create new Address using given data.", BAD_REQUEST);
        }
        return new ResponseEntity<>(addressDto, OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateAddress(@PathVariable long id, @RequestBody AddressDto addressDto) {
        addressDto = addressService.update(id, addressDto);
        if (addressDto == null) {
            return new ResponseEntity<>(format(ADDRESS_NOT_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(addressDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable long id) {
        if (!addressService.delete(id)) {
            return new ResponseEntity<>(format(ADDRESS_NOT_FOUND, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
