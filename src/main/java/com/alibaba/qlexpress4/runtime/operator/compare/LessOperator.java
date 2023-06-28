package com.alibaba.qlexpress4.runtime.operator.compare;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class LessOperator extends BaseBinaryOperator {
    private static final LessOperator INSTANCE = new LessOperator();

    private LessOperator() {
    }

    public static LessOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getOperator() {
        return "<";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.COMPARE;
    }

    @Override
    public Object execute(Value left, Value right, QLOptions qlOptions, ErrorReporter errorReporter) {
        if (qlOptions.isAvoidNullPointer() && (left.get() == null || right.get() == null)) {
            return false;
        }
        return compare(left, right, errorReporter) < 0;
    }
}