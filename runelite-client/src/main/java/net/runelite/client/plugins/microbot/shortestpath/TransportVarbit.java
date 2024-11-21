package net.runelite.client.plugins.microbot.shortestpath;

import lombok.Getter;

@Getter
public class TransportVarbit {
    
    private final int varbitId;
    private final int value;
    private final Operator operator;

    public TransportVarbit(int varbitId, int value, Operator operator) {
        this.varbitId = varbitId;
        this.value = value;
        this.operator = operator;
    }

    public boolean matches(int actualValue) {
        switch (operator) {
            case EQUAL:
                return actualValue == value;
            case GREATER_THAN:
                return actualValue > value;
            case LESS_THAN:
                return actualValue < value;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    public enum Operator {
        EQUAL,
        GREATER_THAN,
        LESS_THAN
    }
}