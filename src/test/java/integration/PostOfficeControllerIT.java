package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.PostOfficeDto;
import com.opinta.entity.PostOffice;
import com.opinta.mapper.PostOfficeMapper;
import com.opinta.service.PostOfficeService;
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
import static org.hamcrest.CoreMatchers.equalTo;

public class PostOfficeControllerIT extends BaseControllerIT {
    private PostOffice postOffice;
    private int postOfficeId = MIN_VALUE;
    @Autowired
    private PostOfficeMapper postOfficeMapper;
    @Autowired
    private PostOfficeService postOfficeService;
    @Autowired
    private TestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        postOffice = testHelper.createPostOffice();
        postOfficeId = (int) postOffice.getId();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deletePostOffice(postOffice);
    }

    @Test
    public void getPostOffices() throws Exception {
        when().
                get("/post-offices").
        then().
                statusCode(SC_OK);
    }

    @Test
    public void getPostOffice() throws Exception {
        when().
                get("/post-offices/{id}", postOfficeId).
        then().
                statusCode(SC_OK).
                body("id", equalTo(postOfficeId));
    }

    @Test
    public void getPostOffice_notFound() throws Exception {
        when().
                get("/post-offices/{id}", postOfficeId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createPostOffice() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/post-office.json");
        jsonObject.put("addressId", (int) testHelper.createAddress().getId());
        jsonObject.put("postcodePoolId", (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        int newPostOfficeId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                when().
                        post("/post-offices").
                then().
                        extract().
                        path("id");

        // check created data
        PostOffice createdPostOffice = postOfficeService.getEntityById(newPostOfficeId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(postOfficeMapper.toDto(createdPostOffice));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deletePostOffice(createdPostOffice);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updatePostOffice() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/post-office.json");
        jsonObject.put("addressId", (int) testHelper.createAddress().getId());
        jsonObject.put("postcodePoolId", (int) testHelper.createPostcodePool().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
        when().
                put("/post-offices/{id}", postOfficeId).
        then().
                statusCode(SC_OK);

        // check updated data
        PostOfficeDto postOfficeDto = postOfficeMapper.toDto(postOfficeService.getEntityById(postOfficeId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(postOfficeDto);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deletePostOffice() throws Exception {
        when().
                delete("/post-offices/{id}", postOfficeId).
        then().
                statusCode(SC_OK);
    }

    @Test
    public void deletePostOffices_notFound() throws Exception {
        when().
                delete("/post-offices/{id}", postOfficeId + 1).
        then().
                statusCode(SC_NOT_FOUND);
    }
}
