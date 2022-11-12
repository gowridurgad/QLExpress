package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * @Operation: define a symbol in local scope
 * @Input: 1 symbol init value
 * @Output: 0
 *
 * Author: DQinYuan
 */
public class DefineLocalInstruction extends QLInstruction {

    private final String variableName;

    private final Class<?> defineClz;

    public DefineLocalInstruction(ErrorReporter errorReporter, String variableName, Class<?> defineClz) {
        super(errorReporter);
        this.variableName = variableName;
        this.defineClz = defineClz;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        qContext.defineLocalSymbol(variableName, defineClz, qContext.pop().get());
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 0;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "DefineLocal " + variableName, debug);
    }
}