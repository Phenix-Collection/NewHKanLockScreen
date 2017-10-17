package com.haokan.pubic.http.request;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class RequestEntity<RequestBody> {
    private RequestHeader header;
    private RequestBody body;

    public RequestHeader getHeader() {
        return header;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }

    public RequestBody getBody() {
        return body;
    }

    public void setBody(RequestBody body) {
        this.body = body;
    }
}
