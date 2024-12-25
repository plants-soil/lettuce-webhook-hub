package com.plantssoil.common.persistence.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * Persistence Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000} if use
 * customized code.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class PersistenceException extends BusinessException {
    private static final long serialVersionUID = -1125809995988416L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_13000;
    public final static int BUSINESS_EXCEPTION_CODE_13001 = 13001;
    public final static int BUSINESS_EXCEPTION_CODE_13002 = 13002;
    public final static int BUSINESS_EXCEPTION_CODE_13003 = 13003;
    public final static int BUSINESS_EXCEPTION_CODE_13004 = 13004;
    public final static int BUSINESS_EXCEPTION_CODE_13005 = 13005;
    public final static int BUSINESS_EXCEPTION_CODE_13006 = 13006;
    public final static int BUSINESS_EXCEPTION_CODE_13007 = 13007;
    public final static int BUSINESS_EXCEPTION_CODE_13008 = 13008;
    public final static int BUSINESS_EXCEPTION_CODE_13009 = 13009;
    public final static int BUSINESS_EXCEPTION_CODE_13010 = 13010;
    public final static int BUSINESS_EXCEPTION_CODE_13011 = 13011;
    public final static int BUSINESS_EXCEPTION_CODE_13012 = 13012;
    public final static int BUSINESS_EXCEPTION_CODE_13013 = 13013;
    public final static int BUSINESS_EXCEPTION_CODE_13014 = 13014;
    public final static int BUSINESS_EXCEPTION_CODE_13015 = 13015;

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public PersistenceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000}
     * @param message Exception message
     */
    public PersistenceException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_13000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000}
     * @param cause throwable which caused the exception
     */
    public PersistenceException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_13000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_14000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_13000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_14000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
