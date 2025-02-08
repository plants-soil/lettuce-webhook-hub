package com.plantssoil.webhook.api;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaResteasyServerCodegen", date = "2025-02-08T22:55:59.560416600+08:00[Asia/Shanghai]")
public class ApiException extends Exception{
    private int code;
    public ApiException (int code, String msg) {
        super(msg);
        this.code = code;
    }
}
