package com.plantssoil.common.httpclient.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * Http Client Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000} if use
 * customized code.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class HttpClientException extends BusinessException {
    private static final long serialVersionUID = -8531914497755678928L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_14000;
    public final static int BUSINESS_EXCEPTION_CODE_14001 = 14001;
    public final static int BUSINESS_EXCEPTION_CODE_14002 = 14002;
    public final static int BUSINESS_EXCEPTION_CODE_14003 = 14003;
    public final static int BUSINESS_EXCEPTION_CODE_14004 = 14004;
    public final static int BUSINESS_EXCEPTION_CODE_14005 = 14005;
    public final static int BUSINESS_EXCEPTION_CODE_14006 = 14006;
    public final static int BUSINESS_EXCEPTION_CODE_14007 = 14007;
    public final static int BUSINESS_EXCEPTION_CODE_14008 = 14008;
    public final static int BUSINESS_EXCEPTION_CODE_14009 = 14009;
    public final static int BUSINESS_EXCEPTION_CODE_14010 = 14010;
    public final static int BUSINESS_EXCEPTION_CODE_14011 = 14011;

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(Throwable cause) {
        super(cause);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public HttpClientException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000}
     * @param message Exception message
     */
    public HttpClientException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_14000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000}
     * @param cause throwable which caused the exception
     */
    public HttpClientException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_14000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_15000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_14000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_15000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
