package com.talha11bu.cloudheap.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class R2Service {

    @Autowired
    private S3Client r2Client;

    @Autowired
    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String sessionId) throws IOException {

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "untitled";
        String fileKey = String.format("%s/%s-%s", sessionId, UUID.randomUUID(), originalFilename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        r2Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return fileKey;
    }

    public int deleteFiles(Collection<String> r2FileKeys){
        if(r2FileKeys.isEmpty()){
            return 0;
        }

        List<ObjectIdentifier> identifiers = r2FileKeys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .collect(Collectors.toList());

        Delete delete = Delete.builder().objects(identifiers).build();

        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();
        try {
            r2Client.deleteObjects(deleteRequest);
            return identifiers.size();
        }catch (S3Exception e){
            System.err.println("R2 Bulk Deletion failed: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Failed to delete Files");
        }
    }
}
