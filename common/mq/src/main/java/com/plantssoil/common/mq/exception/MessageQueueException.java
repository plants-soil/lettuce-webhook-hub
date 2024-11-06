package com.plantssoil.common.mq.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * Message Queue Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000} if use
 * customized code.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class MessageQueueException extends BusinessException {
    private static final long serialVersionUID = -598022603395228697L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_15000;
    public final static int BUSINESS_EXCEPTION_CODE_15001 = 15001;
    public final static int BUSINESS_EXCEPTION_CODE_15002 = 15002;
    public final static int BUSINESS_EXCEPTION_CODE_15003 = 15003;
    public final static int BUSINESS_EXCEPTION_CODE_15004 = 15004;
    public final static int BUSINESS_EXCEPTION_CODE_15005 = 15005;
    public final static int BUSINESS_EXCEPTION_CODE_15006 = 15006;
    public final static int BUSINESS_EXCEPTION_CODE_15007 = 15007;
    public final static int BUSINESS_EXCEPTION_CODE_15008 = 15008;
    public final static int BUSINESS_EXCEPTION_CODE_15009 = 15009;
    public final static int BUSINESS_EXCEPTION_CODE_15010 = 15010;
    public final static int BUSINESS_EXCEPTION_CODE_15011 = 15011;
    public final static int BUSINESS_EXCEPTION_CODE_15012 = 15012;
    public final static int BUSINESS_EXCEPTION_CODE_15013 = 15013;
    public final static int BUSINESS_EXCEPTION_CODE_15014 = 15014;

    public MessageQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageQueueException(String message) {
        super(message);
    }

    public MessageQueueException(Throwable cause) {
        super(cause);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public MessageQueueException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000}
     * @param message Exception message
     */
    public MessageQueueException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_15000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000}
     * @param cause throwable which caused the exception
     */
    public MessageQueueException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_15000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_16000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_15000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_16000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
