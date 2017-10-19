package com.haokan.pubic.http.request;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class RequestEntity_FaGe<RequestBody> {
    private RequestHeader_FaGe header;
    private RequestBody body;

    public RequestHeader_FaGe getHeader() {
        return header;
    }

    public void setHeader(RequestHeader_FaGe header) {
        this.header = header;
    }

    public RequestBody getBody() {
        return body;
    }

    public void setBody(RequestBody body) {
        this.body = body;
    }
}
