package com.talha11bu.cloudheap.model;

import java.time.Duration;

public record JoinResponse(boolean success, Session session, Duration timeLeft, String message) {}
