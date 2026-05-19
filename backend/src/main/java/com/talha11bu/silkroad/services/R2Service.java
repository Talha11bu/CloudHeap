package com.talha11bu.silkroad.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service for interacting with Cloudflare R2 (S3-compatible) object storage.
 *
 * <p>Handles all file operations including uploads, downloads, pre-signed URL
 * generation, and deletions. Supports both legacy server-proxied uploads and
 * the preferred direct-to-client pre-signed URL flow.</p>
 *
 * <p>Also provides a streaming ZIP download that dynamically compresses
 * multiple R2 objects into a single archive on-the-fly using virtual threads.</p>
 *
 * @see com.talha11bu.silkroad.config.R2Config
 */
@Service
public class R2Service {

    @Autowired
    private S3Client r2Client;

    @Autowired
    private S3Presigner r2Presigner;

    @Autowired
    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    /**
     * Uploads a file directly to Cloudflare R2 bucket.
     * Note: This is a legacy server-side upload method. Direct-to-client uploads via pre-signed URLs are preferred.
     *
     * @param file      The MultipartFile to upload.
     * @param sessionId The ID of the session the file belongs to.
     * @return The generated R2 file key.
     * @throws IOException If the file cannot be read.
     */
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

    /**
     * Downloads a single file from R2 and returns it as a Spring {@link Resource}.
     *
     * @param r2FileKey the internal R2 object key.
     * @return an {@link InputStreamResource} wrapping the file's byte stream.
     * @throws NoSuchElementException if the file is not found (HTTP 404 from R2).
     * @throws RuntimeException       if the download fails for any other reason.
     */
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

    /**
     * Generates a pre-signed URL allowing a client to download a file directly from R2.
     * The URL is valid for 5 minutes and enforces the Content-Disposition header so the browser downloads it with the original filename.
     *
     * @param objectKey The internal R2 file key.
     * @param fileName  The original filename to present to the user.
     * @return A secure, time-limited download URL.
     */
    public String generatePreSignedDownloadUrl(String objectKey, String fileName) {
        var getObjectRequest = GetObjectRequest.builder()
                                               .bucket(bucketName)
                                               .key(objectKey)
                                               .responseContentDisposition("attachment; filename=\""+fileName+"\"")
                                               .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)).getObjectRequest(getObjectRequest).build();

        PresignedGetObjectRequest presignedRequest = r2Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    /**
     * Generates a pre-signed URL allowing a client to upload a file directly to R2.
     * This avoids proxying large files through the Spring Boot backend, saving memory and bandwidth.
     * The URL is valid for 15 minutes.
     *
     * @param sessionId        The session ID the file belongs to.
     * @param originalFilename The original name of the file being uploaded.
     * @param contentType      The MIME type of the file.
     * @return A map containing the pre-signed "url" and the generated internal "fileKey".
     */
    public java.util.Map<String, String> generatePreSignedUploadUrl(String sessionId, String originalFilename, String contentType) {
        String fileKey = String.format("%s/%s-%s", sessionId, UUID.randomUUID(), originalFilename);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = r2Presigner.presignPutObject(presignRequest);

        return java.util.Map.of(
            "url", presignedRequest.url().toString(),
            "fileKey", fileKey
        );
    }

    /**
     * Streams multiple files from R2 and dynamically compresses them into a single ZIP archive on-the-fly.
     * Uses a virtual thread to pipe the output stream to avoid keeping large byte arrays in memory.
     *
     * @param r2Keys A list of R2 file keys to include in the ZIP.
     * @return An InputStreamResource representing the dynamic ZIP file stream.
     * @throws IOException If the piped streams fail to initialize.
     */
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

    /**
     * Deletes a single file from the R2 bucket.
     *
     * @param r2FileKey the R2 object key to delete.
     * @throws RuntimeException if the deletion fails.
     */
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

    /**
     * Performs a bulk deletion of multiple files in a single network request to R2.
     * Used primarily when a session expires or is manually closed.
     *
     * @param r2FileKeys A collection of R2 keys to delete.
     * @return The number of successfully deleted files.
     */
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
