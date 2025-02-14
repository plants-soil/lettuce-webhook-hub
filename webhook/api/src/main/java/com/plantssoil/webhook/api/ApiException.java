package com.plantssoil.webhook.api;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-09T17:41:01.999402200+08:00[Asia/Shanghai]")
public class ApiException extends Exception {
    private static final long serialVersionUID = -6098550507237636279L;
    @SuppressWarnings("unused")
    private int code;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
