package com.alibaba.qlexpress4.runtime.operator.compare;

/**
 * @author 冰够
 */
public class EqualOperator extends CompareOperator {
    @Override
    protected boolean execute(int compareResult) {
        return compareResult == 0;
    }

    @Override
    public String getOperator() {
        return "==";
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
