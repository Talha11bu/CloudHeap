package com.talha11bu.silkroad.model;

import java.time.Duration;

public record JoinResponse(boolean success, Session session, Duration timeLeft, String message) {}
