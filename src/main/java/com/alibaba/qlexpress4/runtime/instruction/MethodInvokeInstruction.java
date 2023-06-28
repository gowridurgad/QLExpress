package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMethod;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PrintlnUtils;
import com.alibaba.qlexpress4.utils.PropertiesUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Operation: invoke specified method of object on the top of stack
 * @Input: ${argNum} + 1
 * @Output: 1, method return value, null for void method
 * <p>
 * equivalent to GetMethodInstruction + CallInstruction
 * <p>
 * Author: DQinYuan
 */
public class MethodInvokeInstruction extends QLInstruction {

    private final String methodName;

    private final int argNum;

    public MethodInvokeInstruction(ErrorReporter errorReporter, String methodName, int argNum) {
        super(errorReporter);
        this.methodName = methodName;
        this.argNum = argNum;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters parameters = qContext.pop(this.argNum + 1);
        Object bean = parameters.get(0).get();
        // TODO: 数组遍历优化
        Class<?>[] type = new Class[this.argNum];
        Object[] params = this.argNum > 0 ? new Object[this.argNum] : null;
        for (int i = 0; i < this.argNum; i++) {
            Value v = parameters.get(i + 1);
            params[i] = v.get();
            type[i] = v.getType();
        }
        if (bean == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.NEXT_INSTRUCTION;
            }
            throw errorReporter.report(new NullPointerException(),
                    "GET_METHOD_FROM_NULL", "can not get method from null");
        }
        QLCaches qlCaches = qContext.getQLCaches();
        QLConvertResult convertResult;
        QLImplicitMethod implicitMethod = bean instanceof MetaClass ?
                getClazzMethod(qlCaches, ((MetaClass) bean).getClz(), type, qlOptions.enableAllowAccessPrivateMethod()) :
                getInstanceMethod(qlCaches, bean, type, qlOptions.enableAllowAccessPrivateMethod());
        if (implicitMethod == null) {
            //lambda special
            QLambda qLambdaInnerMethod = findQLambdaInstance(bean);
            if (qLambdaInnerMethod != null) {
                try {
                    QResult qResult = qLambdaInnerMethod.call(params);
                    Value dataValue = new DataValue(qResult.getResult());
                    qContext.push(dataValue);
                } catch (InvocationTargetException e) {
                    throw errorReporter.report(e.getTargetException(), "METHOD_INNER_EXCEPTION", "method inner exception:" + methodName);
                } catch (IllegalAccessException e) {
                    throw errorReporter.report(e, "GET_METHOD_VALUE_CAN_NOT_ACCESS", "can not allow access method:" + methodName);
                } catch (IllegalArgumentException e) {
                    throw errorReporter.report(e, "GET_METHOD_VALUE_WRONG_ARGUMENT", "method param is not match");
                } catch (NullPointerException e) {
                    throw errorReporter.report(e, "GET_METHOD_VALUE_METHOD_NPE", "instance object is null");
                } catch (ExceptionInInitializerError e) {
                    throw errorReporter.report(e.getException(), "GET_METHOD_VALUE_INITIAL_ERROR", "initialization provoked by this method:" + methodName);
                } catch (Throwable e) {
                    throw errorReporter.report(e, "GET_METHOD_VALUE_UNEXPECT", "unexpect error");
                }
            } else {
                throw errorReporter.report("NOT_FIND_LAMBDA_METHOD", "lambda method not exists:" + methodName);
            }
        } else {
            //normal method invoke
            convertResult = ParametersConversion.convert(params, type, implicitMethod.getMethod().getParameterTypes()
                    , implicitMethod.needImplicitTrans(), implicitMethod.getVars());
            if (convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)) {
                throw errorReporter.report("GET_METHOD_VALUE_CAST_PARAM_ERROR", "can not cast param");
            }
            try {
                Object value = MethodHandler.Access.accessMethodValue(implicitMethod.getMethod(), bean,
                        (Object[]) convertResult.getCastValue(), qlOptions.enableAllowAccessPrivateMethod());
                Value dataValue = new DataValue(value);
                qContext.push(dataValue);
            } catch (InvocationTargetException e) {
                throw errorReporter.report(e.getTargetException(), "METHOD_INNER_EXCEPTION", "method inner exception:" + methodName);
            } catch (IllegalAccessException e) {
                throw errorReporter.report(e, "GET_METHOD_VALUE_CAN_NOT_ACCESS", "can not allow access method:" + methodName);
            } catch (IllegalArgumentException e) {
                throw errorReporter.report(e, "GET_METHOD_VALUE_WRONG_ARGUMENT", "method param is not match");
            } catch (NullPointerException e) {
                throw errorReporter.report(e, "GET_METHOD_VALUE_METHOD_NPE", "instance object is null");
            } catch (ExceptionInInitializerError e) {
                throw errorReporter.report(e.getException(), "GET_METHOD_VALUE_INITIAL_ERROR", "initialization provoked by this method:" + methodName);
            } catch (Throwable e) {
                throw errorReporter.report(e, "GET_METHOD_VALUE_UNEXPECT", "unexpect error");
            }
        }

        return QResult.NEXT_INSTRUCTION;
    }


    @Override
    public int stackInput() {
        return argNum + 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "MethodInvoke " + methodName + " with argNum " + argNum, debug);
    }

    public String getMethodName() {
        return methodName;
    }

    public QLImplicitMethod getClazzMethod(QLCaches qlCaches, Object bean, Class<?>[] type, boolean enableAllowAccessPrivateMethod){
        QLImplicitMethod cacheElement = CacheUtil.getMethodInvokeCacheElement(qlCaches.getQlMethodInvokeCache(), (Class<?>) bean , this.methodName, type);
        if (cacheElement == null) {
            List<Method> methods = PropertiesUtil.getClzMethod((Class<?>) bean, this.methodName, enableAllowAccessPrivateMethod);
            QLImplicitMethod implicitMethod = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
            if (implicitMethod == null || implicitMethod.getMethod() == null) {
                return null;
            }
            CacheUtil.setMethodInvokeCacheElement(qlCaches.getQlMethodInvokeCache(), (Class<?>) bean, this.methodName, implicitMethod, type);
            return implicitMethod;
        } else {
            return cacheElement;
        }
    }

    public QLImplicitMethod getInstanceMethod(QLCaches qlCaches, Object bean, Class<?>[] type, boolean enableAllowAccessPrivateMethod) {
        QLImplicitMethod cacheElement = CacheUtil.getMethodInvokeCacheElement(qlCaches.getQlMethodInvokeCache(), bean.getClass(), this.methodName, type);
        if (cacheElement == null) {
            List<Method> methods = PropertiesUtil.getMethod(bean, this.methodName, enableAllowAccessPrivateMethod);
            QLImplicitMethod implicitMethod = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
            if (implicitMethod == null || implicitMethod.getMethod() == null) {
                return null;
            }
            CacheUtil.setMethodInvokeCacheElement(qlCaches.getQlMethodInvokeCache(), bean.getClass(), this.methodName, implicitMethod, type);
            return implicitMethod;
        } else {
            return cacheElement;
        }
    }

    protected QLambda findQLambdaInstance(Object bean) {
        if (bean instanceof Map) {
            Map map = (Map) bean;
            Object mapValue = map.get(methodName);
            if (mapValue instanceof QLambda) {
                return (QLambda) mapValue;
            }
        }
        return null;
    }
}
