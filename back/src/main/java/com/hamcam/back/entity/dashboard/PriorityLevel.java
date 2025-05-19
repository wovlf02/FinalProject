package com.hamcam.back.entity.dashboard;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PriorityLevel {
    LOW(1),
    NORMAL(2),
    HIGH(3);

    private final int level;

    PriorityLevel(int level) {
        this.level = level;
    }

    @JsonValue
    public String getName() {
        return this.name(); // "LOW", "NORMAL", "HIGH"
    }

    public int getLevel() {
        return level;
    }

    @JsonCreator
    public static PriorityLevel from(String value) {
        return PriorityLevel.valueOf(value.toUpperCase());
    }
}
