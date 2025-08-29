package com.github.araujoronald.domain.model;

public enum CustomerQualifier {
    DEFAULT(1),
    VIP(3),
    EXPRESS(5);

    private final int priority;

    CustomerQualifier(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
