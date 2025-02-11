package com.plantssoil.webhook.api;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public class NotFoundException extends ApiException {
    private static final long serialVersionUID = 3005684532074333168L;
    @SuppressWarnings("unused")
    private int code;

    public NotFoundException(int code, String msg) {
        super(code, msg);
        this.code = code;
    }
}
