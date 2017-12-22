package com.gsat.netcdflist.domain.lambda.netcdflist;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetCDFListMetadata {

    @JsonProperty("filesize")
    public String filesize;

    @JsonProperty("created")
    public String created;

    @JsonProperty("url")
    public String url;

    public NetCDFListMetadata(String filesize, String created, String url) {
        this.filesize = filesize;
        this.created = created;
        this.url = url;
    }

    public NetCDFListMetadata() {
    }
}
