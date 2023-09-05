package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.aparser.ImportManager;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:35 下午
 */
public class QLOptions {

    public static QLOptions DEFAULT_OPTIONS = QLOptions.builder().build();

    /**
     * precise evaluate based on BigDecimal
     * default false
     */
    private final boolean precise;

    /**
     * define global symbol in user context
     * default false
     */
    private final boolean polluteUserContext;

    /**
     * script timeout millisecond, default is -1, namely time unlimited
     * <= 0, time unlimited
     * default -1
     */
    private final long timeoutMillis;

    /**
     * attachments will be carried to user defined function/operator/macro
     * only used to pass data, not as variable value
     *
     * default empty map
     */
    private final Map<String, Object> attachments;

    /**
     * allow cache compile result of script
     *
     * default true
     */
    private final boolean cache;

    private final QLSecurityStrategy securityStrategy;

    /**
     * avoid null pointer
     * default false
     */
    private final boolean avoidNullPointer;

    private QLOptions(boolean precise, boolean polluteUserContext, long timeoutMillis,
                      Map<String, Object> attachments, boolean cache,
                      QLSecurityStrategy securityStrategy, boolean avoidNullPointer) {
        this.precise = precise;
        this.polluteUserContext = polluteUserContext;
        this.timeoutMillis = timeoutMillis;
        this.attachments = attachments;
        this.cache = cache;
        this.securityStrategy = securityStrategy;
        this.avoidNullPointer = avoidNullPointer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPrecise() {
        return precise;
    }

    public boolean isPolluteUserContext() {
        return polluteUserContext;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public boolean isCache() {
        return cache;
    }

    public QLSecurityStrategy getSecurityStrategy() {
        return securityStrategy;
    }

    public boolean isAvoidNullPointer() {
        return avoidNullPointer;
    }

    public static class Builder {
        private boolean precise = false;

        private boolean polluteUserContext = false;

        private long timeoutMillis = -1;

        private Map<String, Object> attachments = Collections.emptyMap();

        private boolean cache = true;

        private QLSecurityStrategy securityStrategy;

        private boolean avoidNullPointer = false;

        public Builder precise(boolean precise) {
            this.precise = precise;
            return this;
        }

        public Builder polluteUserContext(boolean polluteUserContext) {
            this.polluteUserContext = polluteUserContext;
            return this;
        }

        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public Builder attachments(Map<String, Object> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder cache(boolean cache) {
            this.cache = cache;
            return this;
        }

        public Builder securityStrategy(QLSecurityStrategy securityStrategy) {
            this.securityStrategy = securityStrategy;
            return this;
        }

        public Builder avoidNullPointer(boolean avoidNullPointer) {
            this.avoidNullPointer = avoidNullPointer;
            return this;
        }

        public QLOptions build() {
            return new QLOptions(precise, polluteUserContext, timeoutMillis,
                attachments, cache, securityStrategy, avoidNullPointer);
        }
    }
}
