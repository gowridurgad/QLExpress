package com.alibaba.qlexpress4.runtime.operator.base;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * @author bingo
 */
public abstract class BaseUnaryOperator implements UnaryOperator {
    protected QLRuntimeException buildInvalidOperandTypeException(Value value, ErrorReporter errorReporter) {
        return errorReporter.reportFormat("InvalidOperandType", "Cannot use %s operator on type:%s with value:%s",
            getOperator(), value.getTypeName(), value.get());
    }
}