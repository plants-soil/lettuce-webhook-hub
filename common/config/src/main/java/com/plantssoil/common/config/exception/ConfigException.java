package com.plantssoil.common.config.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * File System Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000} if use
 * customized code.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class ConfigException extends BusinessException {
    private static final long serialVersionUID = -78822516232994315L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_12000;
    public final static int BUSINESS_EXCEPTION_CODE_12001 = 12001;
    public final static int BUSINESS_EXCEPTION_CODE_12002 = 12002;
    public final static int BUSINESS_EXCEPTION_CODE_12003 = 12003;
    public final static int BUSINESS_EXCEPTION_CODE_12004 = 12004;
    public final static int BUSINESS_EXCEPTION_CODE_12005 = 12005;
    public final static int BUSINESS_EXCEPTION_CODE_12006 = 12006;
    public final static int BUSINESS_EXCEPTION_CODE_12007 = 12007;
    public final static int BUSINESS_EXCEPTION_CODE_12008 = 12008;
    public final static int BUSINESS_EXCEPTION_CODE_12009 = 12009;
    public final static int BUSINESS_EXCEPTION_CODE_12010 = 12010;
    public final static int BUSINESS_EXCEPTION_CODE_12011 = 12011;
    public final static int BUSINESS_EXCEPTION_CODE_12012 = 12012;
    public final static int BUSINESS_EXCEPTION_CODE_12013 = 12013;
    public final static int BUSINESS_EXCEPTION_CODE_12014 = 12014;

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public ConfigException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000}
     * @param message Exception message
     */
    public ConfigException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000}
     * @param cause throwable which caused the exception
     */
    public ConfigException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_12000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_13000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_12000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_13000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
