package com.talha11bu.cloudheap.model;

import java.time.LocalTime;

public record CreateResponse(boolean success, String sessionId, LocalTime expiresAt) {}
