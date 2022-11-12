package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * Author: DQinYuan
 */
public interface Value {
    Value NULL_VALUE = new DataValue((Object)null);

    Object get();

    Class<?> getDefinedType();

    default Class<?> getType() {
        Object value = get();
        if (value == null) {
            return null;
        }

        return value.getClass();
    }

    default String getTypeName() {
        Class<?> type = getType();
        if (type == null) {
            return null;
        }
        return type.getName();
    }
}