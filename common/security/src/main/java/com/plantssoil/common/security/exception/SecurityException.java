package com.plantssoil.common.security.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * Security Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_10000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_10000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000} if use
 * customized code.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class SecurityException extends BusinessException {
    private static final long serialVersionUID = -3443466751031612310L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_10000;
    public final static int BUSINESS_EXCEPTION_CODE_10001 = 10001;
    public final static int BUSINESS_EXCEPTION_CODE_10002 = 10002;
    public final static int BUSINESS_EXCEPTION_CODE_10003 = 10003;
    public final static int BUSINESS_EXCEPTION_CODE_10004 = 10004;
    public final static int BUSINESS_EXCEPTION_CODE_10005 = 10005;
    public final static int BUSINESS_EXCEPTION_CODE_10006 = 10006;
    public final static int BUSINESS_EXCEPTION_CODE_10007 = 10007;
    public final static int BUSINESS_EXCEPTION_CODE_10008 = 10008;
    public final static int BUSINESS_EXCEPTION_CODE_10009 = 10009;
    public final static int BUSINESS_EXCEPTION_CODE_10010 = 10010;
    public final static int BUSINESS_EXCEPTION_CODE_10011 = 10011;
    public final static int BUSINESS_EXCEPTION_CODE_10012 = 10012;
    public final static int BUSINESS_EXCEPTION_CODE_10013 = 10013;
    public final static int BUSINESS_EXCEPTION_CODE_10100 = 10100;
    public final static int BUSINESS_EXCEPTION_CODE_10101 = 10101;
    public final static int BUSINESS_EXCEPTION_CODE_10102 = 10102;
    public final static int BUSINESS_EXCEPTION_CODE_10103 = 10103;
    public final static int BUSINESS_EXCEPTION_CODE_10104 = 10104;

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(Throwable cause) {
        super(cause);
    }

    /**
     * Security Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_10000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public SecurityException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Security Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_10000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000}
     * @param message Exception message
     */
    public SecurityException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Security Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_10000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000}
     * @param cause throwable which caused the exception
     */
    public SecurityException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_10000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_11000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_10000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_11000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
