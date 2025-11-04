package com.talha11bu.cloudheap.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public Resource downloadFile(String r2FileKey){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(r2FileKey)
                .build();

        try{
            return new InputStreamResource(r2Client.getObject(getObjectRequest));
        }catch (S3Exception e){
            if (e.statusCode() ==404){
                throw new NoSuchElementException("File not found " + r2FileKey);
            }
            throw new RuntimeException("Failed to Downlaod from R2 "+ e);
        }
    }

    public Resource donwloadFilesAsZip(List<String> r2Keys) throws IOException{

        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream(outputStream);

        Thread.ofVirtual().name("r2-zipfolder-ceator").start(() ->{
            try(ZipOutputStream zipOut = new ZipOutputStream(outputStream)){

                for(String r2Key : r2Keys){
                    String orignalName = r2Key.substring(r2Key.lastIndexOf('-')+1);

                    zipOut.putNextEntry(new ZipEntry(orignalName));

                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(r2Key)
                            .build();
                    try(InputStream fileInputStream = r2Client.getObject(getObjectRequest)){
                        fileInputStream.transferTo(zipOut);
                    }
                    zipOut.closeEntry();
                }
            } catch (Exception e) {
                System.err.println("Error  during r2 zipping process : " + e.getMessage());
            }finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    System.err.println("Error Closing output stream : " + e.getMessage());
                }
            }
        });
        return new InputStreamResource(inputStream);
    }

    public void deleteFile(String r2FileKey){
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(r2FileKey)
                .build();

        try{
            r2Client.deleteObject(deleteRequest);
        }catch(S3Exception e){
            System.err.println("R2 File Deletion failed for key " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        }
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
