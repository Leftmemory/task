package com.zxd.tast.common.result;

import java.io.Serializable;

/**
 * @author zxd
 * @since 16/12/26.
 */
public class Result<D> implements Serializable {
    private static final long serialVersionUID = -6288926377350146593L;

    private boolean success;
    private int code;
    private String message;
    private D data;

    public static <D> Result<D> wrapSuccess() {
        return wrapSuccess(null);
    }

    public static <D> Result<D> wrapSuccess(D data) {
        Result<D> result = new Result<D>();
        result.success = true;
        result.data = data;
        result.code = 200;
        return result;
    }

    public static <D> Result<D> wrapError() {
        return wrapError(500, null);
    }

    public static <D> Result<D> wrapError(int code, String message) {
        Result<D> result = new Result<D>();
        result.success = false;
        result.data = null;
        result.code = 500;
        result.message = message;
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public D getData() {
        return data;
    }
}
