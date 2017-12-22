package com.gsat.netcdflist.domain.lambda.netcdflist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetCDFListMetadata {

    @JsonProperty("filesize")
    String filesize;

    @JsonProperty("created")
    String created;

    @JsonProperty("url")
    String url;

    public NetCDFListMetadata(String filesize, String created, String url) {
        this.filesize = filesize;
        this.created = created;
        this.url = url;
    }
}
