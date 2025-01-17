package com.plantssoil.webhook.persists.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * Engine Persistence Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_22000} if use
 * customized code.<br/>
 * This exception happens when webhook engine persistence have some issue.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class EnginePersistenceException extends BusinessException {
    private static final long serialVersionUID = -2551546464875482334L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_21000;
    public final static int BUSINESS_EXCEPTION_CODE_21001 = 21001;
    public final static int BUSINESS_EXCEPTION_CODE_21002 = 21002;
    public final static int BUSINESS_EXCEPTION_CODE_21003 = 21003;
    public final static int BUSINESS_EXCEPTION_CODE_21004 = 21004;
    public final static int BUSINESS_EXCEPTION_CODE_21005 = 21005;
    public final static int BUSINESS_EXCEPTION_CODE_21006 = 21006;
    public final static int BUSINESS_EXCEPTION_CODE_21007 = 21007;
    public final static int BUSINESS_EXCEPTION_CODE_21008 = 21008;
    public final static int BUSINESS_EXCEPTION_CODE_21009 = 21009;
    public final static int BUSINESS_EXCEPTION_CODE_21010 = 21010;
    public final static int BUSINESS_EXCEPTION_CODE_21011 = 21011;
    public final static int BUSINESS_EXCEPTION_CODE_21012 = 21012;
    public final static int BUSINESS_EXCEPTION_CODE_21013 = 21013;
    public final static int BUSINESS_EXCEPTION_CODE_21014 = 21014;
    public final static int BUSINESS_EXCEPTION_CODE_21015 = 21015;
    public final static int BUSINESS_EXCEPTION_CODE_21016 = 21016;
    public final static int BUSINESS_EXCEPTION_CODE_21017 = 21017;

    public EnginePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnginePersistenceException(String message) {
        super(message);
    }

    public EnginePersistenceException(Throwable cause) {
        super(cause);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_22000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public EnginePersistenceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_22000}
     * @param message Exception message
     */
    public EnginePersistenceException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_22000}
     * @param cause throwable which caused the exception
     */
    public EnginePersistenceException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_21000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_22000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_21000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_22000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
