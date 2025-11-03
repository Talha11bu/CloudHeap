package com.talha11bu.cloudheap.model;

import org.springframework.web.multipart.MultipartFile;

public record UploadRequest (String sessionId, String password, MultipartFile file){}
