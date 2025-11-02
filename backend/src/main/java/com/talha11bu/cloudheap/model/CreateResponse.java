package com.talha11bu.cloudheap.model;

import java.time.Duration;

public record CreateResponse(boolean success, String sessionId, String password, Duration duration) {}
