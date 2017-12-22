package com.gsat.netcdflist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsat.netcdflist.aws.S3Module;
import com.gsat.netcdflist.domain.lambda.netcdflist.NetCDFListResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class NetCDFListTest {

    S3Module s3Module;

    @Before
    public void setupMocks() {
        this.s3Module = Mockito.mock(S3Module.class);
    }

    @Test
    public void shouldReturnListOfOrderedMetadataObjects() {

        String metadata1 = "{\"filesize\": \"10 MB\", \"created\": \"2017-12-21T20:57:13.980Z\", \"url\": \"url1\"}";
        String metadata2 = "{\"filesize\": \"11 MB\", \"created\": \"2018-12-21T20:57:13.980Z\", \"url\": \"url2\"}";

        ArrayList metadataKeys = new ArrayList<String>();
        metadataKeys.add("key1");
        metadataKeys.add("key2");

        when(this.s3Module.filterKeys("metadata.json")).thenReturn(metadataKeys);
        when(this.s3Module.getObjectContentAsString("key1")).thenReturn(metadata1);
        when(this.s3Module.getObjectContentAsString("key2")).thenReturn(metadata2);

        NetCDFList netCDFList = new NetCDFList(this.s3Module, new ObjectMapper());
        NetCDFListResult result = netCDFList.handleEvent();

        assertEquals(2, result.list.size());
        assertEquals("11 MB", result.list.get(0).filesize);
        assertEquals("2018-12-21T20:57:13.980Z", result.list.get(0).created);
        assertEquals("url2", result.list.get(0).url);

        assertEquals("10 MB", result.list.get(1).filesize);
        assertEquals("2017-12-21T20:57:13.980Z", result.list.get(1).created);
        assertEquals("url1", result.list.get(1).url);
    }

    @Test
    public void shouldReturnEmptyList() {

        when(this.s3Module.filterKeys("metadata.json")).thenReturn(new ArrayList<String>());

        NetCDFList netCDFList = new NetCDFList(this.s3Module, new ObjectMapper());
        NetCDFListResult result = netCDFList.handleEvent();

        assertEquals(0, result.list.size());
    }
}
