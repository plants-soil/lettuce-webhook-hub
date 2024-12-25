package com.plantssoil.common.io.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * Serialize Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_17000} if use
 * customized code.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class SerializeException extends BusinessException {
    private static final long serialVersionUID = -2670570423978650665L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_16000;
    public final static int BUSINESS_EXCEPTION_CODE_16001 = 16001;
    public final static int BUSINESS_EXCEPTION_CODE_16002 = 16002;

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_17000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public SerializeException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_17000}
     * @param message Exception message
     */
    public SerializeException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * Configuration Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_16000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_17000}
     * @param cause throwable which caused the exception
     */
    public SerializeException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_16000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_17000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_16000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_17000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
