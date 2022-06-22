package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.instruction.CallInstruction;
import com.alibaba.qlexpress4.runtime.instruction.GetMethodInstruction;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.test.property.Child;
import com.alibaba.qlexpress4.test.property.ParentParameters;
import com.alibaba.qlexpress4.utils.CacheUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/5/4 上午8:36
 */
public class TestMethodInvokeInstruction {
    /**
     * child.getMethod1 (methodInstruction+callInstruction)
     * getMethod1
     */
    @Test
    public void case1(){
        ErrorReporter errorReporter = new ErrorReporter() {
            @Override
            public QLRuntimeException report(Object attachment, String errorCode, String reason) {
                return null;
            }
            @Override
            public QLRuntimeException report(String errorCode, String reason) {
                return null;
            }
            @Override
            public QLRuntimeException report(String errorCode, String format, Object... args) {
                return null;
            }
        };
        CacheUtil.initCache(128, true);
        MethodInvokeInstruction methodInvokeInstruction = new MethodInvokeInstruction(errorReporter, "getMethod1",2);
        TestQRuntimeParent testQRuntimeParent = new TestQRuntimeParent();
        ParentParameters parentParameters = new ParentParameters();
        parentParameters.push(new Child());
        parentParameters.push(1);
        parentParameters.push(2);
        testQRuntimeParent.setParameters(parentParameters);
        methodInvokeInstruction.execute(testQRuntimeParent, QLOptions.builder().allowAccessPrivateMethod(true).build());
        Assert.assertEquals(testQRuntimeParent.getValue().get(),3);
    }
}
