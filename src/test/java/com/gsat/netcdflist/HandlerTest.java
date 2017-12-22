package com.gsat.netcdflist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsat.netcdflist.domain.lambda.lambda.LambdaResponse;
import com.gsat.netcdflist.domain.lambda.netcdflist.NetCDFListMetadata;
import com.gsat.netcdflist.domain.lambda.netcdflist.NetCDFListResult;
import com.gsat.netcdflist.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class HandlerTest {

    NetCDFList netCDFList;
    OutputStream outputStream;
    Handler handler;
    ObjectMapper mapper;

    @Before
    public void setupMocks() {
        mapper = new ObjectMapper();
        netCDFList = Mockito.mock(NetCDFList.class);
        outputStream = new ByteArrayOutputStream();

        HashMap envVars = new HashMap<String, String>();
        envVars.put("aws_profile", "some-profile");
        envVars.put("aws_region", "ap-southeast-2");
        envVars.put("s3Store", "abc");

        try {
            Utils.setEnvironmentVariables(envVars);
        } catch (java.lang.Exception e) {
            System.out.println("could not set up environment variables");
        }

        handler = new Handler(netCDFList);
    }

    public static String jsonFromFile(String fileName) {

        try {
            return FileUtils.readFileToString(new File(fileName), "utf-8");
        } catch (java.io.IOException e) {
            System.out.println(fileName);
        }

        return null;
    }


    @Test
    public void handlerShouldReturnSuccess() throws IOException, JSONException {

        NetCDFListResult result = new NetCDFListResult();
        result.list.add(new NetCDFListMetadata("10 MB", "2017-12-21T20:56:53.604Z", "url1"));
        result.list.add(new NetCDFListMetadata("11 MB", "2017-12-21T20:56:53.614Z", "url2"));

        when(netCDFList.handleEvent()).thenReturn(result);

        InputStream inputStream = new ByteArrayInputStream("arbitrary string".getBytes(StandardCharsets.UTF_8.name()));
        handler.handleRequest(inputStream, outputStream, null);

        LambdaResponse response = mapper.readValue(outputStream.toString(), LambdaResponse.class);
        JSONAssert.assertEquals(jsonFromFile("src/test/java/com/gsat/netcdflist/fixtures/response/lambda-success-response.json"), response.body, false);
        assertEquals(200, response.statusCode);
    }
}
