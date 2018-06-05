package com.source3g.platform.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by huhuaiyong on 2017/9/11.
 */
@Data
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = -4781082011605001370L;
    private String msg;
    private T data;
    private Boolean status = Boolean.valueOf(false);

    public static <T> ApiResult<T> fail(String msg) {
        ApiResult apiResult = new ApiResult();
        apiResult.status = Boolean.valueOf(false);
        apiResult.msg = msg;

        return apiResult;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult apiResult = new ApiResult();
        apiResult.data = data;
        apiResult.status = Boolean.valueOf(true);
        return apiResult;
    }

    public ApiResult() {
    }

    public ApiResult(Boolean status) {
        this.status = status;
        this.msg = status.booleanValue()?"success":"alreay_finish";
    }

    public ApiResult(String code, String msg, T data) {
        this.msg = msg;
        this.data = data;
        if("-1".equals(code)) {
            this.status = Boolean.valueOf(false);
        } else {
            this.status = Boolean.valueOf(true);
        }

    }

    public ApiResult(T data) {
        this.msg = "success";
        this.data = data;
        this.status = Boolean.valueOf(true);
    }

    public String toString() {
        return "ApiResult{msg=\'" + this.msg + '\'' + ", data=" + this.data + ", status=" + this.status + '}';
    }
}
