package com.plantssoil.common.exception;

/**
 * Business Exception with reference code<br/>
 * 
 * @author danialdy
 * @Date 31 Oct 2024 10:26:07 pm
 */
public abstract class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -6902743036711034329L;

    /**
     * Get exception code for business reference<br/>
     * Every type of business exception has unique code<br/>
     * 
     * @return code
     */
    public abstract int getCode();

    @Override
    public String getMessage() {
        String message = String.format("%d: %s", getCode(), super.getMessage());
        return message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}
