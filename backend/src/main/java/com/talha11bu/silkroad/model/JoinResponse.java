package com.talha11bu.silkroad.model;

import java.time.Duration;

public record JoinResponse(boolean success, String token, Session session, Duration timeLeft) {}
