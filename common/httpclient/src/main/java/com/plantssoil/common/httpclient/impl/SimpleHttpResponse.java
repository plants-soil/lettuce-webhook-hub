package com.plantssoil.common.httpclient.impl;

import com.plantssoil.common.httpclient.IHttpResponse;

class SimpleHttpResponse implements IHttpResponse, java.io.Serializable {
    private static final long serialVersionUID = 2161968250291879599L;
    private int statusCode;
    private String body;

    SimpleHttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getBody() {
        return this.body;
    }

}
