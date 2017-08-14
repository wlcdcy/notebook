package com.example.commons;

import org.slf4j.Logger;

public class LogUtils {
    private LogUtils() {
        super();
    }

    public static void writeDebugExceptionLog(Logger log, Exception e) {
        if (log.isDebugEnabled()) {
            log.debug(e.getMessage(), e);
        }
    }

    public static void writeWarnExceptionLog(Logger log, Exception e) {
        if (log.isWarnEnabled()) {
            log.warn(e.getMessage(), e);
        }
    }
    
    public static void writeDebugLog(Logger log, String debug) {
        if (log.isDebugEnabled()) {
            log.debug(debug);
        }
    }

    public static void writeWarnLog(Logger log, String warn) {
        if (log.isWarnEnabled()) {
            log.warn(warn);
        }
    }

    public static void writeInfoLog(Logger log, String info) {
        if (log.isInfoEnabled()) {
            log.info(info);
        }
    }
}
