package com.gsat.netcdflist.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class S3Module {

    private String bucket;
    private AmazonS3 s3Client;

    @Inject
    public S3Module(
            @Named("s3Store") String bucket,
            AmazonS3 s3Client) {
        this.bucket = bucket;
        this.s3Client = s3Client;
    }


    public List<String> filterKeys(String filter) {

        List<String> metadataKeys = new ArrayList<String>();

        try {
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(this.bucket);
            ListObjectsV2Result result;
            do {
                result = this.s3Client.listObjectsV2(req);

                for (S3ObjectSummary objectSummary :
                        result.getObjectSummaries()) {
                    if (objectSummary.getKey().endsWith(filter)) metadataKeys.add(objectSummary.getKey());
                }
                req.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated() == true);

        } catch (AmazonServiceException ase) {
            System.out.println(ase);
        } catch (AmazonClientException ace) {
            System.out.println(ace);
        }

        return metadataKeys;
    }

    public String getObjectContentAsString(String key) {
        S3Object object = this.s3Client.getObject(new GetObjectRequest(this.bucket, key));
        InputStream inputStream = object.getObjectContent();

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
        } catch (java.io.IOException ioe) {
            System.out.println(ioe);
        }
        return writer.toString();
    }
}
