package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.member.MethodHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:21
 */
public class CacheUtil {
    private static final Map<Object, Boolean> FUNCTION_INTERFACE_CACHE = new ConcurrentHashMap<>();

    public static boolean isFunctionInterface(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return FUNCTION_INTERFACE_CACHE.computeIfAbsent(clazz,
                ignore -> clazz.isInterface() && MethodHandler.hasOnlyOneAbstractMethod(clazz.getMethods()));
    }
}