package com.h2h.pda.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String remoteAddress(HttpServletRequest request) {
        return StringUtils.hasText(request.getHeader("X-FORWARDED-FOR"))
                ? request.getHeader("X-FORWARDED-FOR") : request.getRemoteAddr();
    }

    public static String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static boolean checkPort(Integer portNumber) {
        try {
            int port = portNumber;
            if (port < 0 || port > 65535) {
                return true;
            }
        } catch (NumberFormatException ex) {
            return true;
        }
        return false;
    }
}
