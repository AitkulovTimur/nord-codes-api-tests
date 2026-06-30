package com.nordcodes.api.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static com.nordcodes.api.config.ConfigReader.CONFIG;
import static com.nordcodes.api.utils.GeneralConstants.*;

/**
 * Factory for shared RestAssured request and response specifications.
 */
public final class Specifications {

    private Specifications() {
    }

    /**
     * Builds the base request specification without an API key header.
     *
     * @return request specification for unauthenticated NordCodes API calls
     */
    public static RequestSpecification nordCodesRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setContentType(CONTENT_TYPE_FORM)
                .setAccept(CONTENT_TYPE_JSON);

        if (CONFIG.isDetailedLog()) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }

    /**
     * Builds the request specification with the configured API key header.
     *
     * @return request specification for authenticated NordCodes API calls
     */
    public static RequestSpecification nordCodesRequestSpecWithApiKey() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setContentType(CONTENT_TYPE_FORM)
                .setAccept(CONTENT_TYPE_JSON)
                .addHeader(API_KEY_HEADER_NAME, CONFIG.apiKey());

        if (CONFIG.isDetailedLog()) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }

    /**
     * Builds the common response specification expected from the NordCodes API.
     *
     * @return response specification with JSON content type validation
     */
    public static ResponseSpecification nordCodesResponseSpec() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON);

        if (CONFIG.isDetailedLog()) {
            builder.log(LogDetail.ALL);
        }

        return builder.build();
    }
}
