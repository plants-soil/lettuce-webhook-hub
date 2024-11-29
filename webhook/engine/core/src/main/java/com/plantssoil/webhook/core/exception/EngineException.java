package com.plantssoil.webhook.core.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * Engine Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_20000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_20000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000} if use
 * customized code.<br/>
 * This exception happens when webhook engine core have some issue.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class EngineException extends BusinessException {
    private static final long serialVersionUID = -7291466209100936962L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_20000;
    public final static int BUSINESS_EXCEPTION_CODE_20001 = 20001;
    public final static int BUSINESS_EXCEPTION_CODE_20002 = 20002;
    public final static int BUSINESS_EXCEPTION_CODE_20003 = 20003;
    public final static int BUSINESS_EXCEPTION_CODE_20004 = 20004;
    public final static int BUSINESS_EXCEPTION_CODE_20005 = 20005;
    public final static int BUSINESS_EXCEPTION_CODE_20006 = 20006;
    public final static int BUSINESS_EXCEPTION_CODE_20007 = 20007;
    public final static int BUSINESS_EXCEPTION_CODE_20008 = 20008;
    public final static int BUSINESS_EXCEPTION_CODE_20009 = 20009;
    public final static int BUSINESS_EXCEPTION_CODE_20010 = 20010;
    public final static int BUSINESS_EXCEPTION_CODE_20011 = 20011;
    public final static int BUSINESS_EXCEPTION_CODE_20012 = 20012;
    public final static int BUSINESS_EXCEPTION_CODE_20013 = 20013;
    public final static int BUSINESS_EXCEPTION_CODE_20014 = 20014;
    public final static int BUSINESS_EXCEPTION_CODE_20015 = 20015;

    public EngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public EngineException(String message) {
        super(message);
    }

    public EngineException(Throwable cause) {
        super(cause);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_20000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public EngineException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_20000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000}
     * @param message Exception message
     */
    public EngineException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_20000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_21000}
     * @param cause throwable which caused the exception
     */
    public EngineException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_20000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_20000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_20000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_21000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
