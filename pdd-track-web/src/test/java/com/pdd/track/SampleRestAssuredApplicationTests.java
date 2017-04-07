package com.pdd.track;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.restassured3.RestAssuredRestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.operation.preprocess.RestAssuredPreprocessors.modifyUris;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class SampleRestAssuredApplicationTests {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private RequestSpecification documentationSpec;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        this.documentationSpec = new RequestSpecBuilder()
                .addFilter(RestAssuredRestDocumentation.documentationConfiguration(restDocumentation)).build();
    }

    @Test
    public void sample() throws Exception {
        given(this.documentationSpec)
                .accept("application/json")
                .filter(document("sample",
                            preprocessRequest(modifyUris()
                                    .scheme("http")
                                    .host("localhost")
                                    .port(port)),
                            responseFields(
                                    fieldWithPath("docName").description("Document name"),
                                    fieldWithPath("docOwner").description("Document owner"),
                                    fieldWithPath("docVersion").description("Document version")
                            )
                        )
                )
        .when()
                .port(port)
                .get("/student/timeline/rest-docs/")
        .then()
                .assertThat().statusCode(is(200));
    }
}
