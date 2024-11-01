package com.plantssoil.common.fs.exception;

import com.plantssoil.common.exception.BusinessException;
import com.plantssoil.common.exception.BusinessExceptionCode;

/**
 * File System Exception, with default exception code
 * {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000} <br/>
 * Should between {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000}
 * and {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000} if use
 * customized code.<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 10:22:50 am
 */
public class FileSystemException extends BusinessException {
    private static final long serialVersionUID = -1818118976472501085L;
    private int code = BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_11000;
    public final static int BUSINESS_EXCEPTION_CODE_11001 = 11001;

    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSystemException(String message) {
        super(message);
    }

    public FileSystemException(Throwable cause) {
        super(cause);
    }

    /**
     * File System Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000}
     * @param message Exception message
     * @param cause   throwable which caused the exception
     */
    public FileSystemException(int code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    /**
     * File System Exception Constructor
     * 
     * @param code    Exception Code, between
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000}
     *                and
     *                {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000}
     * @param message Exception message
     */
    public FileSystemException(int code, String message) {
        super(message);
        this.setCode(code);
    }

    /**
     * File System Exception Constructor
     * 
     * @param code  Exception Code, between
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_11000} and
     *              {@link BusinessExceptionCode#BUSINESS_EXCEPTION_CODE_12000}
     * @param cause throwable which caused the exception
     */
    public FileSystemException(int code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(int code) {
        if (code < BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_11000 || code >= BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_12000) {
            String err = String.format("Exception code %d beyond scope %d - %d.", code, BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_11000,
                    BusinessExceptionCode.BUSINESS_EXCEPTION_CODE_12000);
            throw new IllegalArgumentException(err);
        }
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
