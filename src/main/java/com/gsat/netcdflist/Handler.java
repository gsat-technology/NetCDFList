package com.gsat.netcdflist;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsat.netcdflist.domain.lambda.LambdaResponse;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Handler implements RequestStreamHandler {

    private final Map<String, String> responseHeaders;
    private final ObjectMapper mapper;

    public Handler() {
        Map<String, String> map = new HashMap<>();
        map.put("Access-Control-Allow-Origin", "*");
        map.put("ContentType", "application/json");
        this.responseHeaders = Collections.unmodifiableMap(map);

        this.mapper = new ObjectMapper();
    }


    private void writeOutput(String body, int statusCode, OutputStream outputStream) {
        LambdaResponse response = new LambdaResponse(body, statusCode, this.responseHeaders);

        try {
            this.mapper.writeValue(outputStream, response);
        } catch (java.io.IOException e) {
            System.out.println("could not write result");
        }
    }


    public void handleRequest(InputStream inputStream, OutputStream outStream, Context context) {
        StringWriter writer = new StringWriter();

        try {
            IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
            String theString = writer.toString();
            System.out.println(theString);
            writeOutput(theString, 200, outStream);
        } catch (java.io.IOException e) {

        }
    }
}
