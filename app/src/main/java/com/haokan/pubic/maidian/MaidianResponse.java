package com.haokan.pubic.maidian;

/**
 * 检查升级，请求回来的信息封装成的bean
 */
public class MaidianResponse {
    private int code;
    private int err_code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
