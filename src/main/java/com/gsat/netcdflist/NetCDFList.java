package com.gsat.netcdflist;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.gsat.netcdflist.aws.S3Module;
import com.gsat.netcdflist.domain.lambda.netcdflist.NetCDFListMetadata;
import com.gsat.netcdflist.domain.lambda.netcdflist.NetCDFListResult;

import java.util.List;

public class NetCDFList {

    private static String METADATA_KEY = "metadata.json";

    private S3Module s3module;
    private ObjectMapper mapper;

    @Inject
    public NetCDFList(
            S3Module s3module,
            ObjectMapper mapper
    ) {
        this.s3module = s3module;
        this.mapper = mapper;
    }


    public NetCDFListResult handleEvent() {

        NetCDFListResult result = new NetCDFListResult();
        List<String> metadataKeys = s3module.filterKeys(METADATA_KEY);

        metadataKeys.forEach(name -> {
            try {
                String json = s3module.getObjectContentAsString(name);
                NetCDFListMetadata metadata = mapper.readValue(json, NetCDFListMetadata.class);
                result.addMetadataItem(metadata);
            } catch (java.io.IOException ioe) {
                System.out.println(ioe);
            }
        });

        result.sortLatestToEarliest();

        return result;
    }
}
