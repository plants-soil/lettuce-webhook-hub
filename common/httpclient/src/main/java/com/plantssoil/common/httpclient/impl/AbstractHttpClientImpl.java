package com.plantssoil.common.httpclient.impl;

import com.plantssoil.common.httpclient.IHttpClient;

abstract class AbstractHttpClientImpl implements IHttpClient {
    private int maxRequests = 64;
    private int maxRequestsPerHost = 5;
    private int maxIdleConnections = 5;

    @Override
    public IHttpClient maxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
        return this;
    }

    @Override
    public IHttpClient maxRequestsPerHost(int maxRequestsPerHost) {
        this.maxRequestsPerHost = maxRequestsPerHost;
        return this;
    }

    @Override
    public IHttpClient maxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
        return this;
    }

    protected int getMaxRequests() {
        return maxRequests;
    }

    protected int getMaxRequestsPerHost() {
        return maxRequestsPerHost;
    }

    protected int getMaxIdleConnections() {
        return maxIdleConnections;
    }
}
