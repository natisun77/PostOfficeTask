package com.opinta.controller;

import com.opinta.dto.ParcelDto;
import com.opinta.dto.ParcelItemDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.service.PDFGeneratorService;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/shipments")
public class ShipmentController {
    private ShipmentService shipmentService;
    private PDFGeneratorService pdfGeneratorService;
    private ParcelService parcelService;
    private ParcelItemService parcelItemService;
    private final String SHIPMENT_DOES_NOT_EXIST = "No Shipment found for ID %d";
    private final String PARCEL_DOES_NOT_EXIST = "No Parcel found for ID %d";
    private final String PARCELITEM_DOES_NOT_EXIST = "No ParcelItem found for ID %d";
    private final String PDF_EXTENSION = ".pdf";
    private final String CACHE_CONTROL_VALUE = "must-revalidate, post-check=0, pre-check=0";

    @Autowired
    public ShipmentController(ShipmentService shipmentService, PDFGeneratorService pdfGeneratorService,
                              ParcelService parcelService, ParcelItemService parcelItemService) {
        this.shipmentService = shipmentService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.parcelService = parcelService;
        this.parcelItemService = parcelItemService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ShipmentDto> getShipments() {
        return shipmentService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getShipment(@PathVariable("id") long id) {
        ShipmentDto shipmentDto = shipmentService.getById(id);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(SHIPMENT_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @GetMapping("{id}/label-form")
    public ResponseEntity<?> getShipmentLabelForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generateLabel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "labelform" + id + PDF_EXTENSION;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(CACHE_CONTROL_VALUE);
        return new ResponseEntity<>(data, headers, OK);
    }

    @GetMapping("{id}/postpay-form")
    public ResponseEntity<?> getShipmentPostpayForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generatePostpay(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "postpayform" + id + PDF_EXTENSION;
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl(CACHE_CONTROL_VALUE);
        return new ResponseEntity<>(data, headers, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public ShipmentDto createShipment(@RequestBody ShipmentDto shipmentDto) {
        return shipmentService.save(shipmentDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateShipment(@PathVariable long id, @RequestBody ShipmentDto shipmentDto) {
        shipmentDto = shipmentService.update(id, shipmentDto);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format(SHIPMENT_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipment(@PathVariable long id) {
        if (!shipmentService.delete(id)) {
            return new ResponseEntity<>(format(SHIPMENT_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @PostMapping("{shipmentId}/parcels")
    @ResponseStatus(OK)
    public ResponseEntity<?> createParcel(@PathVariable("shipmentId") long shipmentId,
                                          @RequestBody ParcelDto parcelDto) {
        parcelDto = parcelService.save(shipmentId, parcelDto);
        if (parcelDto == null) {
            return new ResponseEntity<>(format(SHIPMENT_DOES_NOT_EXIST, shipmentId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto, OK);
    }

    @DeleteMapping("/parcels/{id}")
    public ResponseEntity<?> deleteParcel(@PathVariable long id) {
        if (!parcelService.delete(id)) {
            return new ResponseEntity<>(format(PARCEL_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @PostMapping("/parcels/{parcelId}/parcelItems")
    @ResponseStatus(OK)
    public ResponseEntity<?> createParcelItem(@PathVariable("parcelId") long parcelId,
                                              @RequestBody ParcelItemDto parcelItemDto) {
        parcelItemDto = parcelItemService.save(parcelId, parcelItemDto);
        if (parcelItemDto == null) {
            return new ResponseEntity<>(format(PARCEL_DOES_NOT_EXIST, parcelId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItemDto, OK);
    }

    @DeleteMapping("/parcels/parcelItems/{id}")
    public ResponseEntity<?> deleteParcelItem(@PathVariable long id) {
        if (!parcelItemService.delete(id)) {
            return new ResponseEntity<>(format(PARCELITEM_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @GetMapping("{id}/parcels")
    public ResponseEntity<?> getParcels(@PathVariable long id) {
        List<ParcelDto> parcelsInShipment = parcelService.getAllByShipmentId(id);
        if (parcelsInShipment == null) {
            return new ResponseEntity<>(format("Parcels in shipment %d doesn't exist", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelsInShipment, OK);
    }

    @GetMapping("/parcels/{id}")
    public ResponseEntity<?> getParcel(@PathVariable long id) {
        ParcelDto parcelDto = parcelService.getById(id);
        if (parcelDto == null) {
            return new ResponseEntity<>(format(PARCEL_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto, OK);
    }

    @GetMapping("/parcels/{id}/parcelItems")
    public ResponseEntity<?> getParcelItems(@PathVariable long id) {
        List<ParcelItemDto> parcelItemsInParcel = parcelItemService.getAllByParcelId(id);
        if (parcelItemsInParcel == null) {
            return new ResponseEntity<>(format("ParcelItems in parcel %d doesn't exist", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItemsInParcel, OK);
    }

    @GetMapping("/parcels/parcelItems/{id}")
    public ResponseEntity<?> getParcelItem(@PathVariable long id) {
        ParcelItemDto parcelItemDto = parcelItemService.getById(id);
        if (parcelItemDto == null) {
            return new ResponseEntity<>(format(PARCELITEM_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItemDto, OK);
    }

    @PutMapping("/parcels/{id}")
    public ResponseEntity<?> updateParcel(@PathVariable long id, @RequestBody ParcelDto parcelDto) {
        parcelDto = parcelService.update(id, parcelDto);
        if (parcelDto == null) {
            return new ResponseEntity<>(format(PARCEL_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto, OK);
    }

    @PutMapping("/parcels/parcelItems/{id}")
    public ResponseEntity<?> updateParcelItem(@PathVariable long id, @RequestBody ParcelItemDto parcelItemDto) {
        parcelItemDto = parcelItemService.update(id, parcelItemDto);
        if (parcelItemDto == null) {
            return new ResponseEntity<>(format(PARCELITEM_DOES_NOT_EXIST, id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItemDto, OK);
    }
}
