package com.talha11bu.silkroad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configuration for Cloudflare R2 (S3-compatible) client beans.
 *
 * <p>Reads R2 credentials and endpoint URL from application properties
 * and configures both an {@link S3Client} for direct operations and an
 * {@link S3Presigner} for generating temporary pre-signed URLs.</p>
 *
 * <p>Both beans use path-style access, which is required by Cloudflare R2,
 * and the {@code "auto"} region as R2 does not use traditional AWS regions.</p>
 */
@Configuration
public class R2Config {

        @Value("${cloudflare.r2.endpoint}")
        private String endpointUrl;

        @Value("${cloudflare.r2.access-key}")
        private String accessKey;

        @Value("${cloudflare.r2.secret-key}")
        private String privateKey;

        /**
         * Configures the primary S3Client to connect to Cloudflare R2 instead of AWS S3.
         * Enforces path-style access (required by R2) and disables chunked encoding for broader compatibility.
         *
         * @return A configured S3Client instance.
         */
        @Bean
        public S3Client r2Client() {

                S3Configuration serviceConfiguration = S3Configuration.builder().pathStyleAccessEnabled(true)
                                .chunkedEncodingEnabled(false).build();

                return S3Client.builder().region(Region.of("auto")).endpointOverride(URI.create(endpointUrl))
                                .serviceConfiguration(serviceConfiguration)
                                .credentialsProvider(StaticCredentialsProvider
                                                .create(AwsBasicCredentials.create(accessKey, privateKey)))
                                .build();
        }

        /**
         * Configures an S3Presigner to generate secure, temporary URLs for direct client-to-R2 uploads/downloads.
         * Crucial for bypassing the Spring Boot backend to save memory and bandwidth during file transfers.
         *
         * @return A configured S3Presigner instance.
         */
        @Bean
        public S3Presigner r2Presigner() {
                return S3Presigner.builder()
                .region(Region.of("auto"))
                                .endpointOverride(URI.create(endpointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(
                                                AwsBasicCredentials.create(accessKey, privateKey)))
                                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
        }
}
