package com.epam.jamp2.model;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexey on 05.12.2016.
 */
public class CalculationCommand {
    private static final Pattern commandPattern;

    private static final String MATCHER_GROUP_LO_CCY = "leftOperandCurrency";
    private static final String MATCHER_GROUP_LO_VAL = "leftOperandValue";
    private static final String MATCHER_GROUP_RO_CCY = "rightOperandCurrency";
    private static final String MATCHER_GROUP_RO_VAL = "rightOperandValue";
    private static final String MATCHER_GROUP_OPERATION = "operation";
    private static final String MATCHER_GROUP_RESULT_CCY = "resultCurrency";

    private Value leftOperand;
    private Value rightOperand;
    private Operation operation;
    private Optional<String> resultCurrencyCode;

    static {
        commandPattern = Pattern.compile("(?<"+MATCHER_GROUP_LO_CCY+">[a-zA-Z]{3})?(?<"+MATCHER_GROUP_LO_VAL+">.*?)" +
                "(?<"+MATCHER_GROUP_OPERATION+">[+|\\-|\\*|/]{1})" +
                "((?<"+MATCHER_GROUP_RO_CCY+">[a-zA-Z]{3})?)(?<"+MATCHER_GROUP_RO_VAL+">.*?)" +
                "=((?<"+MATCHER_GROUP_RESULT_CCY+">[a-zA-Z]{3}))?");
    }

    public CalculationCommand(Value leftOperand, Value rightOperand, Operation operation, String resultCurrencyCode) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operation = operation;
        this.resultCurrencyCode = Optional.ofNullable(resultCurrencyCode);
    }

    public static CalculationCommand parseFromString(String string) throws CommandFormatException {
        Matcher m = commandPattern.matcher(string);
        if(m.matches()) {
            Value leftOperand = new Value(m.group(MATCHER_GROUP_LO_CCY).toUpperCase(), new BigDecimal(m.group(MATCHER_GROUP_LO_VAL)));
            Value rightOperand = new Value(m.group(MATCHER_GROUP_RO_CCY).toUpperCase(), new BigDecimal(m.group(MATCHER_GROUP_RO_VAL)));
            return new CalculationCommand(leftOperand, rightOperand,
                    Operation.fromChar(m.group(MATCHER_GROUP_OPERATION)),
                    m.group(MATCHER_GROUP_RESULT_CCY).toUpperCase());
        } else {
            throw new CommandFormatException();
        }
    }

    public Value getLeftOperand() {
        return leftOperand;
    }

    public void setLeftOperand(Value leftOperand) {
        this.leftOperand = leftOperand;
    }

    public Value getRightOperand() {
        return rightOperand;
    }

    public void setRightOperand(Value rightOperand) {
        this.rightOperand = rightOperand;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Optional<String> getResultCurrencyCode() {
        return resultCurrencyCode;
    }

    public void setResultCurrencyCode(Optional<String> resultCurrencyCode) {
        this.resultCurrencyCode = resultCurrencyCode;
    }
}
