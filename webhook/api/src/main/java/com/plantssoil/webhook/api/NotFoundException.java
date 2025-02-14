package com.plantssoil.webhook.api;

public class NotFoundException extends ApiException {
    private static final long serialVersionUID = 3005684532074333168L;
    @SuppressWarnings("unused")
    private int code;

    public NotFoundException(int code, String msg) {
        super(code, msg);
        this.code = code;
    }
}
