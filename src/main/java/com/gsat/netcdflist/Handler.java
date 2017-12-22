package com.gsat.netcdflist;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.gsat.netcdflist.domain.lambda.lambda.LambdaResponse;
import com.gsat.netcdflist.domain.lambda.netcdflist.NetCDFListResult;
import com.gsat.netcdflist.guice.GuiceModule;
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
    private NetCDFList netCDFList;

    public Handler() {
        Map<String, String> map = new HashMap<>();
        map.put("Access-Control-Allow-Origin", "*");
        map.put("ContentType", "application/json");
        this.responseHeaders = Collections.unmodifiableMap(map);

        AmazonS3 s3Client = (System.getenv("aws_region") != null && System.getenv("aws_profile") != null) ? AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(System.getenv("aws_profile")))
                .withRegion(System.getenv("aws_region"))
                .build() : AmazonS3ClientBuilder.standard().build();

        Injector injector = Guice.createInjector(
                new GuiceModule(
                        System.getenv("s3Store"),
                        s3Client
                )
        );
        this.netCDFList = injector.getInstance(NetCDFList.class);

        this.mapper = new ObjectMapper();
    }

    public Handler(NetCDFList netCDFList) {
        this();
        this.netCDFList = netCDFList;
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

            NetCDFListResult result = netCDFList.handleEvent();

            writeOutput(mapper.writeValueAsString(result), 200, outStream);
        } catch (java.io.IOException ioe) {
            System.out.println(ioe);
        }
    }
}
