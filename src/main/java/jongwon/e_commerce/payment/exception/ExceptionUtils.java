package jongwon.e_commerce.payment.exception;

import org.apache.hc.core5.http.ConnectionRequestTimeoutException;

import java.net.SocketTimeoutException;

public class ExceptionUtils {

    public static boolean hasCause(Throwable t, Class<?> type) {
        while (t != null) {
            if (type.isInstance(t)) return true;
            t = t.getCause();
        }
        return false;
    }

    public static boolean isReadTimeout(Throwable cause) {
        return hasCause(cause, SocketTimeoutException.class)
                && messageContains(cause, "read timed out");
    }

    public static boolean isConnectTimeout(Throwable cause) {
        return hasCause(cause, SocketTimeoutException.class)
                && messageContains(cause, "connect timed out");
    }

    public static boolean isConnectionRequestTimeout(Throwable cause) {
        return hasCause(cause, ConnectionRequestTimeoutException.class);
    }

    private static boolean messageContains(Throwable t, String keyword) {
        while (t != null) {
            if (t.getMessage() != null &&
                    t.getMessage().toLowerCase().contains(keyword)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }

}
