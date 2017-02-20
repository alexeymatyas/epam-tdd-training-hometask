package com.epam.jamp2.model;

/**
 * Created by Alexey on 05.12.2016.
 */
public enum Operation {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/");

    private String sign;

    Operation(String sign) {
        this.sign = sign;
    }

    public static Operation fromChar(String character) {
        for(Operation op: Operation.values()) {
            if(op.sign.equals(character)) {
                return op;
            }
        }
        return null;
    }
}
