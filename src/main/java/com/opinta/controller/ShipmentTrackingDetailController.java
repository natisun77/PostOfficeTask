package com.opinta.controller;

import com.opinta.dto.ShipmentTrackingDetailDto;
import com.opinta.service.ShipmentTrackingDetailService;
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
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/shipment-tracking")
public class ShipmentTrackingDetailController {
    private ShipmentTrackingDetailService shipmentTrackingDetailService;
    private final String SHIPMENT_TRACKING_DETAIL_DOES_NOT_EXIST = "No ShipmentTrackingDetail found for ID %d";

    @Autowired
    public ShipmentTrackingDetailController(ShipmentTrackingDetailService shipmentTrackingDetailService) {
        this.shipmentTrackingDetailService = shipmentTrackingDetailService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ShipmentTrackingDetailDto> getShipmentTrackingDetails() {
        return shipmentTrackingDetailService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getShipmentTrackingDetail(@PathVariable("id") long id) {
        ShipmentTrackingDetailDto shipmentTrackingDetailDto = shipmentTrackingDetailService.getById(id);
        if (shipmentTrackingDetailDto == null) {
            return new ResponseEntity<>(format(SHIPMENT_TRACKING_DETAIL_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentTrackingDetailDto, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public void createShipmentTrackingDetail(@RequestBody ShipmentTrackingDetailDto shipmentTrackingDetailDto) {
        shipmentTrackingDetailService.save(shipmentTrackingDetailDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateShipmentTrackingDetail(
            @PathVariable long id, @RequestBody ShipmentTrackingDetailDto shipmentTrackingDetailDto) {
        shipmentTrackingDetailDto = shipmentTrackingDetailService.update(id, shipmentTrackingDetailDto);
        if (shipmentTrackingDetailDto == null) {
            return new ResponseEntity<>(format(SHIPMENT_TRACKING_DETAIL_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentTrackingDetailDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipmentTrackingDetail(@PathVariable long id) {
        if (!shipmentTrackingDetailService.delete(id)) {
            return new ResponseEntity<>(format(SHIPMENT_TRACKING_DETAIL_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}
