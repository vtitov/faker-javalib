package org.robotframework.javalib.library.faker;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Optional;

public class Logging {
    public enum LogLevel {ERROR, WARN, INFO, DEBUG, TRACE, ALL}

    @Setter
    private static LogLevel logLevel = LogLevel.ALL;
    //static LogLevel logLevel = LogLevel.INFO;

    public static String error(String message, Throwable e) {
        return log(LogLevel.ERROR, Optional.ofNullable(e), message);
    }

    public static String error(String message) {
        return log(LogLevel.ERROR, message);
    }

    public static String error(String message, Object... argv) {
        return log(LogLevel.ERROR, message, argv);
    }

    public static String warn(String message) {
        return log(LogLevel.WARN, message);
    }

    public static String warn(String message, Object... argv) {
        return log(LogLevel.WARN, message, argv);
    }

    public static String info(String message) {
        return log(LogLevel.INFO, message);
    }

    public static String info(String message, Object... argv) {
        return log(LogLevel.INFO, message, argv);
    }

    public static String debug(String message) {
        return log(LogLevel.DEBUG, message);
    }

    public static String debug(String message, Object... argv) {
        return log(LogLevel.DEBUG, message, argv);
    }

    public static String trace(String message) {
        return log(LogLevel.TRACE, message);
    }

    public static String trace(String message, Object... argv) {
        return log(LogLevel.TRACE, message, argv);
    }

    private static String log(LogLevel messageLevel, String message, Object... argv) {
        return log(messageLevel, Optional.empty(), message, argv);
    }

    private static String log(LogLevel messageLevel, Optional<Throwable> e, String message, Object... argv) {
        if(logLevel.compareTo(messageLevel)>=0) {
            String s = String.format("*%s:%d* %s%s",
                messageLevel,
                System.currentTimeMillis(),
                String.format(message, argv),
                e.map(ExceptionUtils::getStackTrace).orElse("")
            );
            System.out.println(s);
            return s;
        } else {
            return "";
        }
    }
}
