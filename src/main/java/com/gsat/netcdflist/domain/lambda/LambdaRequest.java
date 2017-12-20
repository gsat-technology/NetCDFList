package com.gsat.netcdflist.domain.lambda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LambdaRequest {

    @JsonProperty("body")
    public String body;

    public LambdaRequest(@JsonProperty("body") String body) {
        this.body = body;
    }
}
