package com.talha11bu.silkroad.model;

import java.time.Duration;

public record CreateRequest(String username, String password, Duration duration) {
}
