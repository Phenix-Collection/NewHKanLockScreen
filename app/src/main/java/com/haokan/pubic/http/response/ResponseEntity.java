package com.haokan.pubic.http.response;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class ResponseEntity<ResponseBody> {
    private ResponseHeader header;
    private ResponseBody body;

    public ResponseHeader getHeader() {
        return header;
    }

    public void setHeader(ResponseHeader header) {
        this.header = header;
    }

    public ResponseBody getBody() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }
}
