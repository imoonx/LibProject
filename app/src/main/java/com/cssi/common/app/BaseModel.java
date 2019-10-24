package com.cssi.common.app;

import java.io.Serializable;

/**
 * 基类
 * <p>
 * Created by 36238 on 2018/1/3.
 */
public class BaseModel implements Serializable {

    private String message;
    private Integer errCode;
    // time

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the errCode
     */
    public Integer getErrCode() {
        if (null == errCode)
            errCode = -1;
        return errCode;
    }

    /**
     * @param errCode the errCode to set
     */
    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }
}