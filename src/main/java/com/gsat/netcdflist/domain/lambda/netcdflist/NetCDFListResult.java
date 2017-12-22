package com.gsat.netcdflist.domain.lambda.netcdflist;


import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetCDFListResult {

    public List<NetCDFListMetadata> list = new ArrayList();

    public void addMetadataItem(NetCDFListMetadata item) {
        this.list.add(item);
    }

    public void sortLatestToEarliest() {
        list.sort((NetCDFListMetadata m1, NetCDFListMetadata m2) -> {
            Date d1 = null;
            Date d2 = null;

            try {
                d1 = new ISO8601DateFormat().parse(m1.created);
                d2 = new ISO8601DateFormat().parse(m2.created);
            } catch (java.text.ParseException pe) {
                System.out.println(pe);
            }

            int compare = d2.compareTo(d1);
            return compare;
        });
    }
}
