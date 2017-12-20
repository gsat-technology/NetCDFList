package com.gsat.netcdflist.domain.lambda;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class LambdaResponse {

    @JsonProperty("body")
    public String body;

    @JsonProperty("statusCode")
    public int statusCode;

    @JsonProperty("headers")
    public Map<String, String> headers;

    public LambdaResponse(String body, int statusCode, Map<String, String> headers) {
        this.body = body;
        this.statusCode = statusCode;
        this.headers = headers;
    }

    public LambdaResponse() {

    }
}
