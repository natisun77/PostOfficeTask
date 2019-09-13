package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ParcelDto;
import com.opinta.dto.ParcelItemDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ParcelItemMapper;
import com.opinta.mapper.ParcelMapper;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.ShipmentService;
import integration.helper.TestHelper;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    private Shipment shipment;
    private int shipmentId = MIN_VALUE;
    private Parcel parcel;
    private int parcelId = MIN_VALUE;
    private ParcelItem parcelItem;
    private int parcelItemId = MIN_VALUE;

    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private ParcelMapper parcelMapper;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private ParcelItemMapper parcelItemMapper;
    @Autowired
    private ParcelItemService parcelItemService;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        shipment = testHelper.createShipment();
        shipmentId = (int) shipment.getId();
        parcel = testHelper.createParcelForShipment(shipment);
        parcelId = (int) parcel.getId();
        parcelItem = testHelper.createParcelItem(parcel);
        parcelItemId = (int) parcelItem.getId();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipment(shipment);
        testHelper.deleteParcel(parcel);
        testHelper.deleteParcelItem(parcelItem);
    }

    @Test
    public void getShipments() throws Exception {
        when().
                get("/shipments").
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getShipment() throws Exception {
        when().
                get("shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(shipmentId));
    }

    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        int newShipmentId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                        when().
                        post("/shipments").
                        then().
                        extract().
                        path("id");

        // check created data
        Shipment createdShipment = shipmentService.getEntityById(newShipmentId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateShipment() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
                when().
                put("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(shipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        jsonObject.put("price", 0);
        expectedJson = jsonObject.toString();

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteShipment() throws Exception {
        when().
                delete("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        when().
                delete("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void getParcels() throws Exception {
        when().
                get("/shipments/1/parcels").
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcel() throws Exception {
        when().
                get("/shipments/parcels/{id}", parcelId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(parcelId));
    }

    @Test
    public void getParcel_notFound() throws Exception {
        when().
                get("/shipments/parcels/{id}", parcelId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createParcel() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcel.json");
        String expectedJson = jsonObject.toString();

        int newParcelId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                        when().
                        post("/shipments/{id}/parcels", shipmentId).
                        then().
                        extract().
                        path("id");

        // check created data
        Parcel createdParcel = parcelService.getEntityById(newParcelId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(parcelMapper.toDto(createdParcel));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteParcel(createdParcel);
    }
    @Test
    @SuppressWarnings("unchecked")
    public void updateParcel() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcel.json");
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
        when().
                put("/shipments/parcels/{id}", parcelId).
        then().
                statusCode(SC_OK);

        // check updated data
        ParcelDto parcelDto = parcelMapper.toDto(parcelService.getEntityById(parcelId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(parcelDto);

        expectedJson = jsonObject.toString();

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteParcel() throws Exception {
        when().
                delete("/shipments/parcels/{id}", parcelId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteParcel_notFound() throws Exception {
        when().
                delete("/shipments/parcels/{id}", parcelId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void getParcelItems() throws Exception {
        when().
                get("/shipments/parcels/{parcelId}/parcelItems", parcelId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcelItem() throws Exception {
        when().
                get("/shipments/parcels/parcelItems/{id}", parcelItemId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(parcelItemId));
    }

    @Test
    public void getParcelItem_notFound() throws Exception {
        when().
                get("/shipments/parcels/parcelItems/{id}", parcelItemId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createParcelItem() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcelItem.json");
        String expectedJson = jsonObject.toString();

        int newParcelItemId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                when().
                        post("/shipments/parcels/{parcelId}/parcelItems", parcelId).
                then().
                        extract().
                        path("id");

        // check created data
        ParcelItem createdParcelItem = parcelItemService.getEntityById(newParcelItemId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(parcelItemMapper.toDto(createdParcelItem));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteParcelItem(createdParcelItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateParcelItem() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcelItem.json");
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
        when().
                put("/shipments/parcels/parcelItems/{id}", parcelItemId).
        then().
                statusCode(SC_OK);

        // check updated data
        ParcelItemDto parcelItemDto = parcelItemMapper.toDto(parcelItemService.getEntityById(parcelItemId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(parcelItemDto);

        expectedJson = jsonObject.toString();

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteParcelItem() throws Exception {
        when().
                delete("/shipments/parcels/parcelItems/{id}", parcelItemId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteParcelItem_notFound() throws Exception {
        when().
                delete("shipments/parcels/parcelItems/{id}", parcelItemId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }
}
