package com.plantssoil.common.httpclient;

import java.io.IOException;

public interface IHttpCallback {
    /**
     * Called when the request could not be executed due to cancellation, a
     * connectivity problem or timeout. Because networks can fail during an
     * exchange, it is possible that the remote server accepted the request before
     * the failure.
     */
    void onFailure(Exception e);

    /**
     * Called when the HTTP response was successfully returned by the remote server.
     * The callback may proceed to read the response body with
     * {@link IHttpResponse#getBody()}.
     *
     * <p>
     * Note that transport-layer success (receiving a HTTP response code, headers
     * and body) does not necessarily indicate application-layer success:
     * {@code response} may still indicate an unhappy HTTP response code like 404 or
     * 500.
     */
    void onResponse(IHttpResponse response) throws IOException;

}
