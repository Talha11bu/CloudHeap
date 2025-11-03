package com.talha11bu.cloudheap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class R2Config {

    @Value("${cloudflare.r2.endpoint}")
    private String endpointUrl;

    @Value("${cloudflare.r2.access-key}")
    private String accesskey;

    @Value("${cloudflare.r2.secret-key}")
    private String privatekey;

    @Bean
    public S3Client r2Client(){
        return S3Client.builder()
                .region(Region.of("auto"))
                .endpointOverride(URI.create(endpointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accesskey, privatekey)
                ))
                .build();
    }

}
